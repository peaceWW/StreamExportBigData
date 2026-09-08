package cn.sdpjw.export.core.service;

import cn.sdpjw.export.config.ExportConfigProperties;
import cn.sdpjw.export.core.callback.ProgressCallback;
import cn.sdpjw.export.core.enricher.DataEnricher;
import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.core.result.ExportResponse;
import cn.sdpjw.export.core.upload.FileUploadStrategy;
import cn.sdpjw.export.core.upload.FileUploadStrategyFactory;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.router.DataSourceRouter;
import org.apache.ibatis.session.ResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * 扩展流式导出服务（支持文件上传）
 * 
 * @param <T> 实体类型
 */
@Slf4j
@Service
public class ExtendedStreamExportService<T> extends GenericStreamExportService<T> {
    
    @Autowired
    private FileUploadStrategyFactory uploadStrategyFactory;
    
    @Autowired
    private ExportConfigProperties configProperties;
    
    @Autowired(required = false)
    private ExportTaskService exportTaskService;

    @Autowired(required = false)
    private DataSourceRouter dataSourceRouter;
    
    @Autowired
    public ExtendedStreamExportService(
            org.springframework.transaction.PlatformTransactionManager transactionManager,
            ZipFileService zipFileService) {
        super(transactionManager, zipFileService);
    }
    
    /**
     * 执行导出并上传
     *
     * @param taskId                任务ID
     * @param resultHandlerProvider ResultHandler 提供者（在事务中执行查询）
     * @param countProvider         数据统计提供者
     * @param fieldMapper           CSV字段映射器
     * @param fileNamePrefix        文件名前缀
     * @param uploadType            上传类型（"local", "oss", "ftp"等）
     * @param remotePath            远程路径
     * @param progressCallback      进度回调（可选）
     * @param exportDir             导出目录（可选）
     * @param tempDir               临时目录（可选）
     * @param totalRows             总行数（已处理上限逻辑后的实际总行数）
     * @throws IOException IO异常
     */
    public void executeExportTask(
            String taskId,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CountProvider countProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String uploadType,
            String remotePath,
            ProgressCallback progressCallback,
            String exportDir,
            String tempDir,
            long totalRows
    ) throws IOException {
        executeExportTask(taskId, resultHandlerProvider, countProvider, fieldMapper,
                fileNamePrefix, uploadType, remotePath, progressCallback,
                exportDir, tempDir, totalRows, null);
    }

    /**
     * 执行导出并上传
     *
     * @param taskId                任务ID
     * @param resultHandlerProvider ResultHandler 提供者（在事务中执行查询）
     * @param countProvider         数据统计提供者
     * @param fieldMapper           CSV字段映射器
     * @param fileNamePrefix        文件名前缀
     * @param uploadType            上传类型（"local", "oss", "ftp"等）
     * @param remotePath            远程路径
     * @param progressCallback      进度回调（可选）
     * @param exportDir             导出目录（可选）
     * @param tempDir               临时目录（可选）
     * @param totalRows             总行数（已处理上限逻辑后的实际总行数）
     * @param dataEnricher          数据增强器（可选，用于补充第三方业务模块数据）
     * @throws IOException IO异常
     */
    public void executeExportTask(
            String taskId,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CountProvider countProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String uploadType,
            String remotePath,
            ProgressCallback progressCallback,
            String exportDir,
            String tempDir,
            long totalRows,
            DataEnricher<T, T> dataEnricher
    ) throws IOException {
        log.info("执行导出并上传");
        // 1. 执行导出
        ExportResponse<String> exportResponse = executeExport(
            taskId, resultHandlerProvider, countProvider, 
            fieldMapper, fileNamePrefix, progressCallback,
            exportDir, tempDir, totalRows, dataEnricher
        );
        
        // 2. 根据导出结果判断
        if (exportResponse.isNoData()) {
            log.info("导出任务无数据: taskId={}, message={}", taskId, exportResponse.getMessage());
            if (exportTaskService != null) {
                runOnMeta(() -> exportTaskService.updateToFailedNoData(taskId, exportResponse.getMessage()));
            }
            return;
        }

        if (exportResponse.isFail()) {
            log.error("导出任务失败: taskId={}, message={}", taskId, exportResponse.getMessage());
            if (exportTaskService != null) {
                runOnMeta(() -> exportTaskService.updateToFailed(taskId, exportResponse.getMessage()));
            }
            return;
        }

        // 3. 导出成功，继续处理上传
        String localFilePath = exportResponse.getData();
        
        // 4. 上传文件（如果需要）
        String remoteUrl = localFilePath;
        // 明确指定了上传类型且不是local，执行上传
        boolean shouldUpload = !uploadType.equals("local");

        if (shouldUpload) {
            FileUploadStrategy strategy = uploadStrategyFactory.getStrategy(uploadType);
            remoteUrl = strategy.upload(localFilePath, remotePath);
            
            // 5. 清理本地文件（如果配置了自动清理）
            if (configProperties.isCleanupLocal()) {
                Files.deleteIfExists(Paths.get(localFilePath));
                log.info("已清理本地文件: {}", localFilePath);
            }
            
            // 6. 更新ExportTask的file_path字段为remoteUrl
            if (exportTaskService != null && remoteUrl != null) {
                try {
                    File localFile = new File(localFilePath);
                    String filename = localFile.getName();
                    String finalRemoteUrl = remoteUrl;
                    long actualRows = totalRows;
                    ExportTask task = runOnMeta(() -> exportTaskService.getByTaskId(taskId));
                    if (task != null && task.getProcessedRows() != null) {
                        actualRows = task.getProcessedRows();
                    }
                    long finalActualRows = actualRows;
                    runOnMeta(() -> exportTaskService.updateToSuccess(taskId, finalRemoteUrl, filename, finalActualRows));
                    log.info("已更新ExportTask为成功: taskId={}, remoteUrl={}", taskId, remoteUrl);
                } catch (Exception e) {
                    log.warn("更新ExportTask失败: taskId={}, remoteUrl={}", taskId, remoteUrl, e);
                    runOnMeta(() -> exportTaskService.updateToFailed(taskId, e.getMessage()));
                }
            }
        } else if (exportTaskService != null) {
            runOnMeta(() -> exportTaskService.updateToSuccess(
                    taskId, localFilePath, new File(localFilePath).getName(), totalRows));
            log.info("导出任务完成: taskId={}, 文件={}, 实际导出行数={}", taskId, localFilePath, totalRows);
        }
    }

    private void runOnMeta(Runnable action) {
        if (dataSourceRouter != null) {
            dataSourceRouter.runOnMeta(action);
        } else {
            action.run();
        }
    }

    private <T> T runOnMeta(java.util.function.Supplier<T> action) {
        if (dataSourceRouter != null) {
            return dataSourceRouter.executeOnMeta(action);
        }
        return action.get();
    }
}

