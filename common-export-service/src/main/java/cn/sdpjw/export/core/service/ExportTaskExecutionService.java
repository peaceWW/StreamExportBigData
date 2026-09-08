package cn.sdpjw.export.core.service;


import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.export.config.ExportConfigProperties;
import cn.sdpjw.export.core.callback.ProgressCallback;
import cn.sdpjw.export.core.context.ExportContext;
import cn.sdpjw.export.core.enricher.DataEnricher;
import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.core.result.ExportResponse;
import cn.sdpjw.export.core.upload.FileUploadStrategy;
import cn.sdpjw.export.core.upload.FileUploadStrategyFactory;
import cn.sdpjw.export.entity.ExportTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 导出任务执行服务（处理导出任务创建和执行逻辑）
 * 
 * @param <T> 实体类型
 */
@Slf4j
@Service
public class ExportTaskExecutionService<T> {
    
    @Autowired
    private ExtendedStreamExportService<T> exportService;
    
    @Autowired
    private ExportTaskService exportTaskService;
    
    @Autowired
    private ExportConfigProperties configProperties;
    
    @Autowired(required = false)
    private FileUploadStrategyFactory uploadStrategyFactory;
    
    // 自注入，用于在同一类内调用异步方法
    private ExportTaskExecutionService<T> self;
    
    @Autowired
    @Lazy
    public void setSelf(ExportTaskExecutionService<T> self) {
        this.self = self;
    }
    
    /**
     * 创建导出任务（带业务参数）
     */
    public String createExportTask(
            Integer operatorEmployeeId,
            String menuCode,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix

    ) {
        return createExportTask(operatorEmployeeId,menuCode,countProvider,resultHandlerProvider,fieldMapper,fileNamePrefix,null);
    }

    /**
     * 创建导出任务（带业务参数）
     */
    public String createExportTask(
            Integer operatorEmployeeId,
            String menuCode,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String remotePath
    ) {
        return createExportTask(operatorEmployeeId, menuCode, countProvider, resultHandlerProvider,
                fieldMapper, fileNamePrefix, remotePath, null);
    }

    /**
     * 创建导出任务（带业务参数和数据增强器）
     */
    public String createExportTask(
            Integer operatorEmployeeId,
            String menuCode,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String remotePath,
            DataEnricher<T, T> dataEnricher
    ) {
        String taskId = UUID.randomUUID().toString().replace("-", "");

        exportTaskService.createTask(taskId,operatorEmployeeId,menuCode);

        // 异步处理（使用 self 调用，确保 @Async 生效）
        self.executeExportTask(
                taskId,
                countProvider,
                resultHandlerProvider,
                fieldMapper,
                fileNamePrefix,
                remotePath,
                dataEnricher
        );

        return taskId;
    }

    /**
     * 直接导出数据
     */
    public ExportTask exportData(
            Integer operatorEmployeeId,
            String menuCode,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String remotePath
    ) {
        return exportData(operatorEmployeeId, menuCode, countProvider, resultHandlerProvider,
                fieldMapper, fileNamePrefix, remotePath, null);
    }

    /**
     * 直接导出数据（带数据增强器）
     */
    public ExportTask exportData(
            Integer operatorEmployeeId,
            String menuCode,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String remotePath,
            DataEnricher<T, T> dataEnricher
    ) {
        String taskId = UUID.randomUUID().toString().replace("-", "");

        exportTaskService.createTask(taskId,operatorEmployeeId,menuCode);

        return self.exportData(
                taskId,
                countProvider,
                resultHandlerProvider,
                fieldMapper,
                fileNamePrefix,
                remotePath,
                dataEnricher
        );
    }

    
    /**
     * 异步执行导出并上传
     * 上传类型从配置文件 export.stream.upload-type 获取
     */
    @Async
    public void executeExportTask(
            String taskId,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String remotePath
    ) {
        executeExportTask(taskId, countProvider, resultHandlerProvider, fieldMapper,
                fileNamePrefix, remotePath, null);
    }

    /**
     * 异步执行导出并上传（带数据增强器）
     * 上传类型从配置文件 export.stream.upload-type 获取
     */
    @Async
    public void executeExportTask(
            String taskId,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String remotePath,
            DataEnricher<T, T> dataEnricher
    ) {
        if (countProvider == null || resultHandlerProvider == null || 
            fieldMapper == null || fileNamePrefix == null) {
            exportTaskService.updateToFailed(taskId,"导出任务参数不完整");
            return;
        }
        // 如果是上传必须填写上传路径
        boolean shouldUpload = !configProperties.getUploadType().equals("local");
        if (shouldUpload && StringUtils.isEmpty(remotePath)) {
            exportTaskService.updateToFailed(taskId,"导出任务远程文件服务器地址参数不完整");
            return;
        }


        // 更新任务状态为运行中
        exportTaskService.updateToRunning(taskId);

        // 1. 统计总数
        long totalRows = countProvider.count();
        log.info("开始导出任务: taskId={}, 查询到的总行数={}", taskId, totalRows);

        // 2. 处理导出条数上限逻辑
        Integer maxExportRows = configProperties.getMaxExportRows();
        long actualTotalRows = totalRows;
        Integer limitValue = null;
        
        if (maxExportRows != null && maxExportRows > 0) {
            if (totalRows >= maxExportRows) {
                actualTotalRows = maxExportRows;
                limitValue = maxExportRows;
            }
        }
        
        // 3. 设置导出上下文（标识当前线程处于导出执行状态，并设置导出限制值）
        try {
            if (limitValue != null) {
                ExportContext.setExportLimit(limitValue);
                log.debug("设置导出上下文: taskId={}, limit={}", taskId, limitValue);
            }

            // 定义进度回调
            ProgressCallback progressCallback = (tid, processedRows, total) -> {
                exportTaskService.updateProgress(tid, processedRows, total);
            };

            //4. 执行导出并上传（使用 ResultHandler 实现真正的流式处理）
            exportService.executeExportTask(
                    taskId,
                    resultHandlerProvider,
                    countProvider,
                    fieldMapper,
                    fileNamePrefix,
                    configProperties.getUploadType(),
                    remotePath,
                    progressCallback,
                    configProperties.getExportDir(),
                    configProperties.getTempDir() + "/" + taskId,
                    actualTotalRows,
                    dataEnricher
            );
        } catch (IOException e) {
            log.error("导出并上传IO异常: taskId={}", taskId, e);
            ExportResponse.fail("导出并上传IO异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("导出并上传异常: taskId={}", taskId, e);
            ExportResponse.fail("导出并上传异常: " + e.getMessage());
        } finally {
            //5. 清除导出上下文（确保不影响后续查询）
            ExportContext.clear();
            log.debug("清除导出上下文: taskId={}", taskId);
        }
    }

    /**
     * 执行导出并上传
     * 上传类型从配置文件 export.stream.upload-type 获取
     */
    public ExportTask exportData(
            String taskId,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String remotePath
    ) {
        return exportData(taskId, countProvider, resultHandlerProvider, fieldMapper,
                fileNamePrefix, remotePath, null);
    }

    /**
     * 执行导出并上传（带数据增强器）
     * 上传类型从配置文件 export.stream.upload-type 获取
     */
    public ExportTask exportData(
            String taskId,
            CountProvider countProvider,
            Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            String remotePath,
            DataEnricher<T, T> dataEnricher
    ) {
        if (countProvider == null || resultHandlerProvider == null ||
                fieldMapper == null || fileNamePrefix == null) {
            exportTaskService.updateToFailed(taskId,"导出任务参数不完整");;
            return exportTaskService.getByTaskId(taskId);
        }
        // 如果是上传必须填写上传路径
        boolean shouldUpload = !configProperties.getUploadType().equals("local");
        if (shouldUpload && StringUtils.isEmpty(remotePath)) {
            exportTaskService.updateToFailed(taskId,"导出任务远程文件服务器地址参数不完整");
            return exportTaskService.getByTaskId(taskId);
        }

        // 更新任务状态为运行中
        exportTaskService.updateToRunning(taskId);

        // 1. 统计总数
        long totalRows = countProvider.count();
        log.info("开始导出任务: taskId={}, 查询到的总行数={}", taskId, totalRows);

        // 2. 处理导出条数上限逻辑
        Integer maxExportRows = configProperties.getMaxExportRows();
        long actualTotalRows = totalRows;
        Integer limitValue = null;

        if (maxExportRows != null && maxExportRows > 0) {
            if (totalRows >= maxExportRows) {
                actualTotalRows = maxExportRows;
                limitValue = maxExportRows;
            }
        }

        // 3. 设置导出上下文（标识当前线程处于导出执行状态，并设置导出限制值）
        try {
            if (limitValue != null) {
                ExportContext.setExportLimit(limitValue);
                log.debug("设置导出上下文: taskId={}, limit={}", taskId, limitValue);
            }

            // 定义进度回调
            ProgressCallback progressCallback = (tid, processedRows, total) -> {
                exportTaskService.updateProgress(tid, processedRows, total);
            };

            //4. 执行导出并上传（使用 ResultHandler 实现真正的流式处理）
            exportService.executeExportTask(
                    taskId,
                    resultHandlerProvider,
                    countProvider,
                    fieldMapper,
                    fileNamePrefix,
                    configProperties.getUploadType(),
                    remotePath,
                    progressCallback,
                    configProperties.getExportDir(),
                    configProperties.getTempDir() + "/" + taskId,
                    actualTotalRows,
                    dataEnricher
            );
        } catch (IOException e) {
            exportTaskService.updateToFailed(taskId,"导出并上传IO异常");
        } catch (Exception e) {
            log.error("导出并上传异常: taskId={}", taskId, e);
            exportTaskService.updateToFailed(taskId,"导出并上传异常");
        } finally {
            //5. 清除导出上下文（确保不影响后续查询）
            ExportContext.clear();
            log.debug("清除导出上下文: taskId={}", taskId);
        }
        return exportTaskService.getByTaskId(taskId);
    }
    
    /**
     * 获取任务进度
     */
    public ExportTask getTaskProgress(String taskId) {
        ExportTask task = exportTaskService.getByTaskId(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在: " + taskId);
        }
        return task;
    }
    
    /**
     * 获取导出文件
     * 支持oss、ftp、local三种情况
     */
    public File getExportFile(String taskId) throws IOException {
        ExportTask task = exportTaskService.getByTaskId(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在: " + taskId);
        }
        
        if (task.getStatus() != ExportTask.TaskStatus.SUCCESS) {
            throw new BusinessException("导出任务未完成");
        }
        
        String filePath = task.getExportUrl();
        if (filePath == null || filePath.isEmpty()) {
            throw new BusinessException("导出文件路径为空");
        }
        
        // 判断文件路径类型
        String uploadType = determineUploadType(filePath);
        
        if ("local".equals(uploadType)) {
            // 本地文件，直接返回
            File file = new File(filePath);
            if (!file.exists()) {
                throw new BusinessException("导出文件不存在: " + filePath);
            }
            return file;
        } else {
            // 远程文件（OSS/FTP），需要下载到临时文件
            FileUploadStrategy strategy = uploadStrategyFactory.getStrategy(uploadType);
            if (strategy == null) {
                throw new BusinessException("不支持的上传类型: " + uploadType);
            }
            
            // 下载到临时文件
            File tempFile = File.createTempFile("export_", "_" + taskId);
            tempFile.deleteOnExit();
            
            try (InputStream inputStream = strategy.download(filePath)) {
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("从{}下载文件到临时文件: taskId={}, remoteUrl={}, tempFile={}", 
                        uploadType, taskId, filePath, tempFile.getAbsolutePath());
            }
            
            return tempFile;
        }
    }
    
    /**
     * 根据filePath判断上传类型
     */
    private String determineUploadType(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "local";
        }
        
        // 检查是否是OSS URL
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            if (filePath.contains("oss") && filePath.contains("aliyuncs.com")) {
                return "oss";
            } else if (filePath.startsWith("ftp://")) {
                return "ftp";
            }
        }
        
        // 默认为本地文件
        return "local";
    }
}

