package cn.sdpjw.export.service;


import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.export.core.adapter.ExportCoreAdapter;
import cn.sdpjw.export.core.service.ExportTaskService;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.handler.ExportHandlerRegistry;
import cn.sdpjw.export.handler.ExportRequestResolver;
import cn.sdpjw.export.router.DataSourceRouter;
import cn.sdpjw.export.stub.dto.ExportRequest;
import cn.sdpjw.export.stub.dto.ExportSyncResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 导出任务编排服务。
 * <p>
 * 负责异步提交、同步导出（含行数分流）、重试及实际执行调度。
 * Sync = Synchronous（同步），Async = Asynchronous（异步）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportExecuteService {

    /**
     * 同步/异步分流阈值：超过则强制异步（写死，不配置）
     */
    public static final long SYNC_ASYNC_THRESHOLD = 50_000L;

    private final ExportRecordServiceImpl exportRecordService;
    private final ExportHandlerRegistry exportHandlerRegistry;
    private final DataSourceRouter dataSourceRouter;
    private final ExportCoreAdapter exportCoreAdapter;
    private final ExportConcurrencyLimiter exportConcurrencyLimiter;
    private final ExportCancelRegistry exportCancelRegistry;
    private final ExportTaskService exportTaskService;

    private ExportExecuteService self;

    /**
     * 注入自身代理，供同类内调用 {@link #executeAsync} 时保证 {@code @Async} 生效。
     */
    @org.springframework.beans.factory.annotation.Autowired
    @Lazy
    public void setSelf(ExportExecuteService self) {
        this.self = self;
    }

    /**
     * 提交异步导出任务。
     * <p>
     * 初始化元数据、注册取消标记后投递到导出线程池，立即返回任务记录（不等待执行完成）。
     *
     * @param request 导出请求
     * @return 已创建的任务记录（含 taskId）
     */
    public ExportTask submit(ExportRequest request) {
        String dataSourceKey = dataSourceRouter.resolve(request.getMenuCode());
        exportHandlerRegistry.getHandler(request.getMenuCode());
        ExportTask record = exportRecordService.initExportTask(request, dataSourceKey);
        exportCancelRegistry.register(record.getTaskId());
        self.executeAsync(record);
        return record;
    }

    /**
     * 同步导出入口（Synchronous）：先 count，再按阈值分流。
     * <ul>
     *   <li>count &le; 0：抛出业务异常，不创建任务</li>
     *   <li>count &gt; {@link #SYNC_ASYNC_THRESHOLD}：强制异步，返回降级结果（degraded=true）</li>
     *   <li>count &le; 阈值：当前线程同步执行，返回终态结果（含 exportUrl）</li>
     * </ul>
     *
     * @param request 导出请求
     * @return 同步终态或降级异步后的结果
     */
    public ExportSyncResult submitSync(ExportRequest request) {
        String dataSourceKey = dataSourceRouter.resolve(request.getMenuCode());
        ExportHandler<?> handler = exportHandlerRegistry.getHandler(request.getMenuCode());
        // HTTP 入口先按 ExportRequest 反序列化会丢掉业务字段，优先用原始 JSON
        ExportRequest typedRequest = StringUtils.hasText(request.getRawRequestJson())
                ? ExportRequestResolver.resolve(handler, request.getRawRequestJson())
                : ExportRequestResolver.resolve(handler, request);

        long totalRows = countRows(dataSourceKey, handler, typedRequest);
        if (totalRows <= 0) {
            throw new BusinessException("未查询到数据");
        }

        if (totalRows > SYNC_ASYNC_THRESHOLD) {
            ExportTask record = exportRecordService.initExportTask(request, dataSourceKey);
            exportCancelRegistry.register(record.getTaskId());
            self.executeAsync(record);
            log.info("同步导出因超过阈值降级为异步, taskId={}, totalRows={}, threshold={}",
                    record.getTaskId(), totalRows, SYNC_ASYNC_THRESHOLD);
            return buildSyncResult(
                    record.getTaskId(),
                    ExportSyncResult.MODE_ASYNC,
                    true,
                    totalRows,
                    "数据量超过" + SYNC_ASYNC_THRESHOLD + "，已转为异步导出");
        }

        ExportTask record = exportRecordService.initExportTask(request, dataSourceKey);
        exportCancelRegistry.register(record.getTaskId());
        executeSync(record);
        return buildSyncResult(
                record.getTaskId(),
                ExportSyncResult.MODE_SYNC,
                false,
                totalRows,
                "同步导出完成");
    }

    /**
     * 重试失败或可恢复状态的导出任务（异步重新执行）。
     *
     * @param taskId 任务 ID
     * @throws BusinessException 任务不存在或当前状态不可重试时抛出
     */
    public void retry(String taskId) {
        ExportTask record = exportRecordService.getByTaskId(taskId);
        if (record == null) {
            throw new BusinessException("任务不存在: " + taskId);
        }
        ExportTask.TaskStatus status = record.getStatus();
        if (status != ExportTask.TaskStatus.FAILED
                && status != ExportTask.TaskStatus.RECOVERABLE) {
            throw new BusinessException("当前状态不可重试: " + status);
        }
        rebuildRequest(record);
        exportRecordService.incrementRetryCount(taskId);
        exportCancelRegistry.register(taskId);
        self.executeAsync(record);
    }

    /**
     * 异步执行导出（Asynchronous）。
     * <p>
     * 由 {@code exportTaskExecutor} 线程池调度，不阻塞提交方线程。
     *
     * @param record 导出任务
     */
    @Async("exportTaskExecutor")
    public void executeAsync(ExportTask record) {
        doExecute(record);
    }

    /**
     * 同步执行导出（Synchronous）。
     * <p>
     * 无 {@code @Async}，阻塞当前线程直至 {@link #doExecute} 结束。
     *
     * @param record 导出任务
     */
    public void executeSync(ExportTask record) {
        doExecute(record);
    }

    /**
     * 导出执行公共逻辑：获取并发槽位 → 调用核心适配器 → 异常落库 → 释放槽位。
     * <p>
     * 供 {@link #executeAsync} 与 {@link #executeSync} 共用。
     *
     * @param record 导出任务
     */
    private void doExecute(ExportTask record) {
        try {
            exportConcurrencyLimiter.acquire();
            ExportHandler<?> handler = exportHandlerRegistry.getHandler(record.getMenuCode());
            exportCoreAdapter.execute(record, handler);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            dataSourceRouter.runOnMeta(() -> exportRecordService.updateRecoverable(record.getTaskId()));
        } catch (Exception ex) {
            log.error("导出失败, taskId={}", record.getTaskId(), ex);
            dataSourceRouter.runOnMeta(() ->
                    exportTaskService.updateToFailed(record.getTaskId(), ex.getMessage()));
        } finally {
            exportConcurrencyLimiter.release();
        }
    }

    /**
     * 在业务数据源上统计导出行数，用于同步入口分流判断。
     *
     * @param dataSourceKey 业务数据源 key
     * @param handler       业务 Handler
     * @param typedRequest  已解析的业务请求 DTO
     * @return 总行数
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private long countRows(String dataSourceKey, ExportHandler handler, ExportRequest typedRequest) {
        ExportCheckpoint checkpoint = new ExportCheckpoint();
        checkpoint.setPartNo(0);
        return dataSourceRouter.execute(dataSourceKey, () ->
                handler.countProvider(typedRequest, checkpoint).count());
    }

    /**
     * 根据任务终态（或当前态）组装同步接口返回体。
     *
     * @param taskId       任务 ID
     * @param syncMode     {@link ExportSyncResult#MODE_SYNC} 或 {@link ExportSyncResult#MODE_ASYNC}
     * @param degraded     是否因超阈值从同步降级为异步
     * @param preCountRows 前置 count 结果（任务尚未写入 totalRows 时回退使用）
     * @param message      提示信息
     * @return 同步导出结果
     */
    private ExportSyncResult buildSyncResult(String taskId, String syncMode, boolean degraded,
                                             Long preCountRows, String message) {
        ExportTask record = exportRecordService.getByTaskId(taskId);
        ExportSyncResult result = new ExportSyncResult();
        result.setTaskId(taskId);
        result.setSyncMode(syncMode);
        result.setDegraded(degraded);
        result.setMessage(message);
        if (record == null) {
            result.setTotalRows(preCountRows);
            return result;
        }
        ExportTask.TaskStatus statusEnum = record.getStatus();
        result.setStatus(statusEnum == null ? null : statusEnum.getCode());
        result.setStatusDesc(statusEnum == null ? null : statusEnum.getDesc());
        result.setExportUrl(record.getExportUrl());
        result.setFilename(record.getFilename());
        result.setFileFormat(record.getFileFormat());
        result.setProcessedRows(record.getProcessedRows());
        result.setTotalRows(record.getTotalRows() != null ? record.getTotalRows() : preCountRows);
        result.setProgress(record.getProgress());
        result.setErrorMessage(record.getErrorMessage());
        return result;
    }

    /**
     * 按任务已存 queryParams 反序列化为业务请求（重试前校验 Handler / 参数可用）。
     *
     * @param record 导出任务
     * @return 业务请求 DTO
     */
    private ExportRequest rebuildRequest(ExportTask record) {
        ExportHandler<?> handler = exportHandlerRegistry.getHandler(record.getMenuCode());
        return ExportRequestResolver.resolve(handler, record.getQueryParams());
    }


}
