package cn.sdpjw.export.controller;

import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.common.base.page.PageData;
import cn.sdpjw.common.base.response.CommonResponse;
import cn.sdpjw.export.core.service.ExportTaskExecutionService;
import cn.sdpjw.export.core.service.ExportTaskService;
import cn.sdpjw.export.core.upload.FileUploadStrategy;
import cn.sdpjw.export.core.upload.FileUploadStrategyFactory;
import cn.sdpjw.export.dto.ExportTaskQueryDTO;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.stub.dto.ExportProgressVO;
import cn.sdpjw.export.stub.dto.ExportRecordQuery;
import cn.sdpjw.export.stub.dto.ExportRecordVO;
import cn.sdpjw.export.stub.dto.ExportRequest;
import cn.sdpjw.export.stub.dto.ExportSyncResult;
import cn.sdpjw.export.stub.provider.ExportProvider;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommonExportController {

    private final ExportProvider exportProvider;

    @Autowired(required = false)
    private ExportTaskExecutionService<?> exportTaskExecutionService;

    @Autowired(required = false)
    private FileUploadStrategyFactory uploadStrategyFactory;

    @Autowired(required = false)
    private ExportTaskService exportTaskService;

    /**
     * 分页条件查询导出任务（GET方式，使用查询参数）
     *
     * @param queryDTO 查询条件DTO（通过@ModelAttribute接收）
     * @return 分页结果
     */
    @GetMapping("/list")
    public CommonResponse<PageData<ExportTask>> listTasksByGet(ExportTaskQueryDTO queryDTO) {
        if (exportTaskService == null) {
            throw new IllegalStateException("ExportTaskService未初始化");
        }
        PageData<ExportTask> exportTaskPageData = exportTaskService.pageQuery(queryDTO);
        return CommonResponse.success(exportTaskPageData);
    }

    @PostMapping("/create")
    public CommonResponse<String> createExport(@RequestBody String body) {
        ExportRequest request = JSON.parseObject(body, ExportRequest.class);
        request.setRawRequestJson(body);
        return exportProvider.createExport(request);
    }

    /**
     * 同步导出：数据量 ≤ 50000 时阻塞至完成并返回结果；超过阈值自动降级异步。
     */
    @PostMapping("/createSync")
    public CommonResponse<ExportSyncResult> createExportSync(@RequestBody String body) {
        ExportRequest request = JSON.parseObject(body, ExportRequest.class);
        request.setRawRequestJson(body);
        return exportProvider.createExportSync(request);
    }

    @GetMapping("/progress/{taskId}")
    public CommonResponse<ExportProgressVO> getProgress(@PathVariable String taskId) {
        ExportProgressVO progress = exportProvider.getProgress(taskId);
        return CommonResponse.success(progress);
    }

    @PostMapping("/records")
    public CommonResponse<PageData<ExportRecordVO>> listRecords(@RequestBody ExportRecordQuery query) {
        return exportProvider.listRecords(query);
    }

    @GetMapping("/getDownloadUrl/{taskId}")
    public CommonResponse<String> getDownloadUrl(@PathVariable String taskId) {
        String downloadUrl = exportProvider.getDownloadUrl(taskId);
        return CommonResponse.success(downloadUrl);
    }

    @PostMapping("/cancel/{taskId}")
    public Map<String, Object> cancelExport(@PathVariable String taskId) {
        exportProvider.cancelExport(taskId);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 200);
        result.put("message", "取消成功");
        return result;
    }

    @PostMapping("/retry/{taskId}")
    public Map<String, Object> retryExport(@PathVariable String taskId) {
        exportProvider.retryExport(taskId);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 200);
        result.put("message", "重试成功");
        return result;
    }

    /**
     * 下载导出文件
     * 支持从OSS、FTP、本地文件系统下载
     */
    @GetMapping("/download/{taskId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String taskId) throws IOException {
        if (exportTaskExecutionService == null) {
            throw new IllegalStateException("ExportTaskExecutionService未初始化");
        }

        ExportTask task = exportTaskExecutionService.getTaskProgress(taskId);
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

        byte[] fileContent;
        String filename = task.getFilename() != null ? task.getFilename() : "export_file";

        // 判断是否是OSS URL
        if (isOssUrl(filePath) && uploadStrategyFactory != null) {
            // 从OSS下载
            FileUploadStrategy strategy = uploadStrategyFactory.getStrategy("oss");
            if (strategy != null) {
                try (InputStream inputStream = strategy.download(filePath)) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[8192];
                    int nRead;
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    fileContent = buffer.toByteArray();
                    log.info("从OSS下载文件成功: taskId={}, filePath={}", taskId, filePath);
                }
            } else {
                throw new BusinessException("OSS策略未配置");
            }
        } else {
            // 本地文件
            File file = exportTaskExecutionService.getExportFile(taskId);
            fileContent = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(fileContent);
            }
            if (task.getFilename() == null || task.getFilename().isEmpty()) {
                filename = file.getName();
            }
        }

        filename = filename.trim();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // JDK 1.8 兼容编码
        String encoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");

        // 正确的 RFC 5987 格式，不可改动
        String contentDisposition = "attachment; filename=" + encoded;

        // 只能 add 一次！不能 add 两次，也不能和 setContentDispositionFormData 混用
        headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    /**
     * 判断是否是OSS URL
     */
    private boolean isOssUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return (url.startsWith("http://") || url.startsWith("https://"))
                && url.contains("oss") && url.contains("aliyuncs.com");
    }
}