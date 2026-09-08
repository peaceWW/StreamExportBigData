package cn.sdpjw.export.provider;

import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.common.base.page.PageData;
import cn.sdpjw.common.base.response.CommonResponse;
import cn.sdpjw.export.auth.ExportAuthChecker;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.service.ExportCancelRegistry;
import cn.sdpjw.export.service.ExportExecuteService;
import cn.sdpjw.export.service.ExportRecordServiceImpl;
import cn.sdpjw.export.service.ExportRequestValidator;
import cn.sdpjw.export.stub.dto.ExportProgressVO;
import cn.sdpjw.export.stub.dto.ExportRecordQuery;
import cn.sdpjw.export.stub.dto.ExportRecordVO;
import cn.sdpjw.export.stub.dto.ExportRequest;
import cn.sdpjw.export.stub.dto.ExportSyncResult;
import cn.sdpjw.export.stub.provider.ExportProvider;
import cn.sdpjw.page.core.PageInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.StringUtils;

/**
 * 公共导出 Dubbo 实现
 * <p>
 * Sync = Synchronous（同步），Async = Asynchronous（异步），二者含义不同。
 */
@Slf4j
@DubboService
@RequiredArgsConstructor
public class ExportProviderImpl implements ExportProvider {

    private final ExportRequestValidator exportRequestValidator;
    private final ExportAuthChecker exportAuthChecker;
    private final ExportExecuteService exportExecuteService;
    private final ExportRecordServiceImpl exportRecordService;
    private final ExportCancelRegistry exportCancelRegistry;

    /**
     * 创建异步导出任务。
     * <p>
     * 校验并鉴权后提交任务，立即返回 taskId；实际导出在后台线程执行。
     *
     * @param request 导出请求
     * @return 任务 ID
     */
    @Override
    public CommonResponse<String> createExport(ExportRequest request) {
        exportRequestValidator.validate(request);
        exportAuthChecker.checkCreatePermission(request.getUserInfo().getEmployeeId());
        ExportTask record = exportExecuteService.submit(request);
        return CommonResponse.success(record.getTaskId());
    }

    /**
     * 同步导出（Synchronous，非异步）。
     * <p>
     * 先 count：未超过 50000 则当前请求内执行完毕并返回终态（含 exportUrl）；
     * 超过阈值则降级为异步，返回 taskId，并通过 {@link ExportSyncResult#getDegraded()} 标识。
     *
     * @param request 导出请求
     * @return 同步终态结果，或降级异步后的结果
     */
    @Override
    public CommonResponse<ExportSyncResult> createExportSync(ExportRequest request) {
        exportRequestValidator.validate(request);
        exportAuthChecker.checkCreatePermission(request.getUserInfo().getEmployeeId());
        ExportSyncResult result = exportExecuteService.submitSync(request);
        return CommonResponse.success(result);
    }

    /**
     * 查询导出任务进度与状态。
     *
     * @param taskId 任务 ID
     * @return 进度信息；任务不存在时可能为 null
     */
    @Override
    public ExportProgressVO getProgress(String taskId) {
        return exportRecordService.getProgress(taskId);
    }

    /**
     * 分页查询导出记录列表。
     *
     * @param query 查询条件（员工、菜单、状态、时间等）
     * @return 分页记录
     */
    @Override
    public CommonResponse<PageData<ExportRecordVO>> listRecords(ExportRecordQuery query) {
        log.info("ExportProviderImpl listRecords parameters:{}",query);
        PageData<ExportRecordVO> page = PageInvoker.page(
                () -> exportRecordService.listRecords(query),
                query
        );
        return CommonResponse.success(page);
    }

    /**
     * 获取已成功任务的文件下载地址。
     *
     * @param taskId 任务 ID
     * @return 下载 URL
     * @throws BusinessException 任务不存在、未完成或地址为空时抛出
     */
    @Override
    public String getDownloadUrl(String taskId) {
        ExportTask record = exportRecordService.getByTaskId(taskId);
        if (record == null) {
            throw new BusinessException("任务不存在: " + taskId);
        }
        if (ExportTask.TaskStatus.SUCCESS != record.getStatus()) {
            throw new BusinessException("任务未完成，无法下载");
        }
        if (!StringUtils.hasText(record.getExportUrl())) {
            throw new BusinessException("下载地址不存在");
        }
        return record.getExportUrl();
    }

    /**
     * 取消运行中的导出任务。
     *
     * @param taskId 任务 ID
     * @throws BusinessException 任务不存在或非 RUNNING 状态时抛出
     */
    @Override
    public void cancelExport(String taskId) {
        ExportTask record = exportRecordService.getByTaskId(taskId);
        if (record == null) {
            throw new BusinessException("任务不存在: " + taskId);
        }
        if (ExportTask.TaskStatus.RUNNING != record.getStatus()) {
            throw new BusinessException("仅运行中任务可取消");
        }
        exportCancelRegistry.cancel(taskId);
    }

    /**
     * 重试失败或可恢复状态的导出任务（异步重新执行）。
     *
     * @param taskId 任务 ID
     */
    @Override
    public void retryExport(String taskId) {
        exportExecuteService.retry(taskId);
    }
}
