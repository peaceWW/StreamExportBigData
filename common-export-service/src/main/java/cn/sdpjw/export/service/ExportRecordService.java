package cn.sdpjw.export.service;

import cn.sdpjw.common.base.page.PageData;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.stub.dto.ExportPageResult;
import cn.sdpjw.export.stub.dto.ExportProgressVO;
import cn.sdpjw.export.stub.dto.ExportRecordQuery;
import cn.sdpjw.export.stub.dto.ExportRecordVO;
import cn.sdpjw.export.stub.dto.ExportRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * 导出记录服务（仅维护 export_data_task 扩展字段，执行态由 core ExportTaskService 写入）
 */
public interface ExportRecordService {

    /**
     * 通过 core 创建任务行，再 UPDATE 业务扩展字段
     */
    ExportTask initExportTask(ExportRequest request, String dataSourceKey);

    /**
     * 更新断点续传扩展字段
     */
    void updateCheckpoint(String taskId, String checkpointLastId, String tempFilePath, Integer partNo);

    /**
     * 导出成功后的扩展字段（fileFormat、exportTime）
     */
    void updateExtensionOnSuccess(String taskId, String fileFormat);

    /**
     * 标记为可恢复（业务扩展状态，core 无对应枚举）
     */
    void updateRecoverable(String taskId);

    ExportTask getByTaskId(String taskId);

    ExportProgressVO getProgress(String taskId);

    List<ExportRecordVO> listRecords(ExportRecordQuery query);


    List<ExportTask> list(LambdaQueryWrapper<ExportTask> wrapper);

    void incrementRetryCount(String taskId);
}