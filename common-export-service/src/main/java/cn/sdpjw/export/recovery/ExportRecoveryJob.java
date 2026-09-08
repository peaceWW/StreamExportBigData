package cn.sdpjw.export.recovery;

import cn.sdpjw.export.config.ExportProperties;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.service.ExportExecuteService;
import cn.sdpjw.export.service.ExportRecordServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 中断任务扫描与恢复
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExportRecoveryJob {

    private final ExportRecordServiceImpl exportRecordService;
    private final ExportExecuteService exportExecuteService;
    private final ExportProperties exportProperties;

    @Scheduled(fixedDelayString = "${export.recovery.scan-interval-ms:60000}")
    public void scanStaleTasks() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(exportProperties.getLimit().getRunningTimeoutMinutes());
        LambdaQueryWrapper<ExportTask> wrapper = new LambdaQueryWrapper<ExportTask>()
                .eq(ExportTask::getStatus, ExportTask.TaskStatus.RUNNING)
                .lt(ExportTask::getUpdatedAt, threshold);
        List<ExportTask> staleTasks = exportRecordService.list(wrapper);
        for (ExportTask record : staleTasks) {
            log.warn("检测到僵死导出任务, taskId={}", record.getTaskId());
            exportRecordService.updateRecoverable(record.getTaskId());
        }
    }

    @Scheduled(fixedDelayString = "${export.recovery.retry-interval-ms:120000}")
    public void autoRetryRecoverableTasks() {
        LambdaQueryWrapper<ExportTask> wrapper = new LambdaQueryWrapper<ExportTask>()
                .eq(ExportTask::getStatus, ExportTask.TaskStatus.RECOVERABLE)
                .lt(ExportTask::getRetryCount, exportProperties.getLimit().getMaxRetry());
        List<ExportTask> tasks = exportRecordService.list(wrapper);
        for (ExportTask record : tasks) {
            try {
                log.info("自动恢复导出任务, taskId={}", record.getTaskId());
                exportExecuteService.retry(record.getTaskId());
            } catch (Exception ex) {
                log.error("自动恢复失败, taskId={}", record.getTaskId(), ex);
            }
        }
    }
}
