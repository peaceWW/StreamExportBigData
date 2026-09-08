package cn.sdpjw.export.service;


import cn.sdpjw.export.core.service.ExportTaskService;
import cn.sdpjw.export.dao.ExportRecordMapper;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.stub.dto.ExportProgressVO;
import cn.sdpjw.export.stub.dto.ExportRecordQuery;
import cn.sdpjw.export.stub.dto.ExportRecordVO;
import cn.sdpjw.export.stub.dto.ExportRequest;
import cn.sdpjw.export.stub.dto.ExportUserInfo;
import cn.sdpjw.export.stub.enums.MenuEnum;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@DS("meta")
@RequiredArgsConstructor
public class ExportRecordServiceImpl implements ExportRecordService {

    private final ExportRecordMapper exportRecordMapper;
    private final ExportTaskService exportTaskService;

    @Override
    public ExportTask initExportTask(ExportRequest request, String dataSourceKey) {
        ExportUserInfo userInfo = request.getUserInfo();
        String taskId = generateTaskId();
        Integer operatorId = userInfo == null ? null : userInfo.getEmployeeId();
        exportTaskService.createTask(taskId, operatorId, request.getMenuCode());

        ExportTask extension = new ExportTask();
        extension.setMenuCode(request.getMenuCode());
        extension.setOperatorName(userInfo == null ? null : userInfo.getEmployeeName());
        extension.setSubscriberApp(userInfo == null ? null : userInfo.getSubscriberApp());
        extension.setDataSourceKey(dataSourceKey);
        extension.setQueryParams(toQueryParamsJson(request));
        extension.setRetryCount(0);
        extension.setCheckpointPartNo(0);
        extension.setReceivedAt(LocalDateTime.now());
        exportRecordMapper.update(extension, byTaskId(taskId));

        return getByTaskId(taskId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCheckpoint(String taskId, String checkpointLastId, String tempFilePath, Integer partNo) {
        ExportTask update = new ExportTask();
        update.setCheckpointLastId(checkpointLastId);
        update.setTempFilePath(tempFilePath);
        if (partNo != null) {
            update.setCheckpointPartNo(partNo);
        }
        exportRecordMapper.update(update, byTaskId(taskId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateExtensionOnSuccess(String taskId, String fileFormat) {
        ExportTask update = new ExportTask();
        update.setFileFormat(fileFormat);
        update.setExportTime(LocalDateTime.now());
        exportRecordMapper.update(update, byTaskId(taskId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateRecoverable(String taskId) {
        ExportTask update = new ExportTask();
        update.setStatus(ExportTask.TaskStatus.RECOVERABLE);
        exportRecordMapper.update(update, byTaskId(taskId));
    }

    @Override
    public ExportTask getByTaskId(String taskId) {
        return exportRecordMapper.selectOne(byTaskId(taskId));
    }

    @Override
    public ExportProgressVO getProgress(String taskId) {
        ExportTask record = getByTaskId(taskId);
        if (record == null) {
            return null;
        }
        ExportProgressVO vo = new ExportProgressVO();
        vo.setTaskId(record.getTaskId());
        ExportTask.TaskStatus statusEnum = record.getStatus();
        vo.setStatus(statusEnum == null ? null : statusEnum.getCode());
        vo.setStatusDesc(statusEnum == null ? null : statusEnum.getDesc());
        vo.setProgress(record.getProgress());
        vo.setProcessedRows(record.getProcessedRows());
        vo.setTotalRows(record.getTotalRows());
        vo.setExportUrl(record.getExportUrl());
        vo.setErrorMessage(record.getErrorMessage());
        return vo;
    }

    @Override
    public List<ExportRecordVO> listRecords(ExportRecordQuery query) {
        List<ExportRecordVO> exportRecordVOS = exportRecordMapper.listRecords(query);
        for (ExportRecordVO exportRecordVO : exportRecordVOS) {
            exportRecordVO.setStatusDesc(ExportTask.TaskStatus.fromCode(exportRecordVO.getStatus()).getDesc());
        }
        return exportRecordVOS;
    }



    public ExportCheckpoint toCheckpoint(ExportTask record) {
        ExportCheckpoint checkpoint = new ExportCheckpoint();
        checkpoint.setProcessedRows(record.getProcessedRows());
        checkpoint.setLastId(record.getCheckpointLastId());
        checkpoint.setPartNo(record.getCheckpointPartNo());
        checkpoint.setTempFilePath(record.getTempFilePath());
        return checkpoint;
    }

    @Override
    public List<ExportTask> list(LambdaQueryWrapper<ExportTask> wrapper) {
        return exportRecordMapper.selectList(wrapper);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementRetryCount(String taskId) {
        ExportTask record = getByTaskId(taskId);
        if (record == null) {
            return;
        }
        ExportTask update = new ExportTask();
        update.setRetryCount(record.getRetryCount() == null ? 1 : record.getRetryCount() + 1);
        exportRecordMapper.update(update, byTaskId(taskId));
    }

    private LambdaQueryWrapper<ExportTask> byTaskId(String taskId) {
        return new LambdaQueryWrapper<ExportTask>().eq(ExportTask::getTaskId, taskId);
    }

    private String generateTaskId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String toQueryParamsJson(ExportRequest request) {
        if (StringUtils.hasText(request.getRawRequestJson())) {
            return request.getRawRequestJson();
        }
        return JSON.toJSONString(request);
    }
}