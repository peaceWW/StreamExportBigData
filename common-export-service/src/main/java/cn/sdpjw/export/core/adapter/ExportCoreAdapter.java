package cn.sdpjw.export.core.adapter;


import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.export.config.ExportConfigProperties;
import cn.sdpjw.export.config.ExportProperties;
import cn.sdpjw.export.core.callback.ProgressCallback;
import cn.sdpjw.export.core.context.ExportContext;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.core.service.ExportTaskService;
import cn.sdpjw.export.core.service.ExtendedStreamExportService;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.handler.ExportRequestResolver;
import cn.sdpjw.export.router.DataSourceRouter;
import cn.sdpjw.export.service.ExportCancelRegistry;
import cn.sdpjw.export.service.ExportRecordServiceImpl;
import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * batch-export-core 适配层
 * <p>
 * 方案 A：执行态字段（status/progress/url/rows）仅由 ExportTaskService 写入；
 * 业务扩展字段（queryParams/checkpoint/fileFormat 等）由 ExportRecordService 写入。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExportCoreAdapter {

    private final ExtendedStreamExportService extendedStreamExportService;
    private final ExportTaskService exportTaskService;
    private final ExportConfigProperties exportConfigProperties;
    private final ExportProperties exportProperties;
    private final DataSourceRouter dataSourceRouter;
    private final ExportRecordServiceImpl exportRecordService;
    private final ExportCancelRegistry exportCancelRegistry;

    public void execute(ExportTask record, ExportHandler handler) {
        String taskId = record.getTaskId();
        ExportCheckpoint checkpoint = exportRecordService.toCheckpoint(record);
        runOnMeta(() -> exportTaskService.updateToRunning(taskId));

        dataSourceRouter.execute(record.getDataSourceKey(), () -> {
            runExport(taskId, handler, record, checkpoint);
            return null;
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void runExport(String taskId, ExportHandler handler, ExportTask record, ExportCheckpoint checkpoint) {
        ExportRequest typedRequest = ExportRequestResolver.resolve(handler, record.getQueryParams());
        CountProvider countProvider = handler.countProvider(typedRequest, checkpoint);
        Consumer<ResultHandler<Object>> streamProvider = handler.streamProvider(typedRequest, checkpoint);

        Integer exportLimit = exportConfigProperties.getMaxExportRows();
        if (exportLimit != null && exportLimit > 0) {
            ExportContext.setExportLimit(exportLimit);
        }
        try {
            long totalRows = countProvider.count();
            if (totalRows <= 0) {
                runOnMeta(() -> exportTaskService.updateToFailedNoData(taskId, "未查询到数据"));
                return;
            }
            if (exportLimit != null && exportLimit > 0 && totalRows > exportLimit) {
                totalRows = exportLimit;
            }

            ProgressCallback progressCallback = (tid, processed, total) -> {
                if (exportCancelRegistry.isCancelled(taskId)) {
                    throw new BusinessException("导出任务已取消");
                }
                runOnMeta(() -> {
                    exportTaskService.updateProgress(taskId, processed, total);
                    exportRecordService.updateCheckpoint(
                            taskId, checkpoint.getLastId(), checkpoint.getTempFilePath(), checkpoint.getPartNo());
                });
            };

            ExportProperties.Stream stream = exportProperties.getStream();
            extendedStreamExportService.executeExportTask(
                    taskId,
                    streamProvider,
                    countProvider,
                    handler.fieldMapper(),
                    handler.fileNamePrefix(typedRequest),
                    stream.getUploadType(),
                    handler.remotePath(typedRequest),
                    progressCallback,
                    stream.getExportDir(),
                    stream.getTempDir() + "/" + taskId,
                    totalRows
            );

            fillExtensionOnComplete(taskId);
        } catch (Exception ex) {
            log.error("导出任务执行失败, taskId={}", taskId, ex);
            if (exportCancelRegistry.isCancelled(taskId)) {
                runOnMeta(() -> exportTaskService.updateToFailed(taskId, "导出任务已取消"));
            } else if (!isCoreTerminal(taskId)) {
                runOnMeta(() -> exportTaskService.updateToFailed(taskId, ex.getMessage()));
            }
        } finally {
            ExportContext.clear();
            exportCancelRegistry.clear(taskId);
        }
    }

    private void fillExtensionOnComplete(String taskId) {
        runOnMeta(() -> {
            ExportTask coreTask = exportTaskService.getByTaskId(taskId);
            if (coreTask != null && coreTask.getStatus() == ExportTask.TaskStatus.SUCCESS) {
                String fileFormat = exportProperties.getStream().isZipEnabled() ? "zip" : "xlsx";
                exportRecordService.updateExtensionOnSuccess(taskId, fileFormat);
            }
        });
    }

    private boolean isCoreTerminal(String taskId) {
        return Boolean.TRUE.equals(dataSourceRouter.executeOnMeta(() -> {
            ExportTask coreTask = exportTaskService.getByTaskId(taskId);
            if (coreTask == null || coreTask.getStatus() == null) {
                return false;
            }
            return coreTask.getStatus() == ExportTask.TaskStatus.SUCCESS
                    || coreTask.getStatus() == ExportTask.TaskStatus.FAILED;
        }));
    }

    private void runOnMeta(Runnable action) {
        dataSourceRouter.runOnMeta(action);
    }
}
