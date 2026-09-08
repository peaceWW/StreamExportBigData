package cn.sdpjw.export.core.service;


import cn.sdpjw.common.base.basic.IdWorker;
import cn.sdpjw.common.base.page.PageData;
import cn.sdpjw.export.dao.ExportTaskMapper;
import cn.sdpjw.export.dto.ExportTaskQueryDTO;
import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.page.core.PageInvoker;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 导出任务服务（CRUD操作）
 */
@Slf4j
@Service
@DS("meta")
public class ExportTaskService {
    
    @Autowired
    private ExportTaskMapper exportTaskMapper;
    
    /**
     * 创建导出任务
     */
    @Transactional
    public ExportTask createTask(String taskId) {
        ExportTask task = new ExportTask();
        Long id = IdWorker.getFlowIdWorkerInstance().nextId();
        task.setId(id);
        task.setTaskId(taskId);
        task.setStatus(ExportTask.TaskStatus.PENDING);
        task.preInsert();
        exportTaskMapper.insert(task);
        return task;
    }

    /**
     * 创建导出任务
     */
    @Transactional
    public void createTask(String taskId, Integer operatorEmployeeId, String menuCode) {
        ExportTask task = new ExportTask();
        Long id = IdWorker.getFlowIdWorkerInstance().nextId();
        task.setId(id);
        task.setTaskId(taskId);
        task.setOperatorEmployeeId(operatorEmployeeId);
        task.setMenuCode(menuCode);
        task.setStatus(ExportTask.TaskStatus.PENDING);
        task.preInsert();
        exportTaskMapper.insert(task);
    }
    
    /**
     * 根据任务ID查询
     */
    public ExportTask getByTaskId(String taskId) {
        return exportTaskMapper.selectByTaskId(taskId);
    }
    
    /**
     * 更新任务状态为运行中
     * 使用 REQUIRES_NEW 确保在独立事务中执行，避免与流式查询的连接冲突
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void updateToRunning(String taskId) {
        ExportTask task = exportTaskMapper.selectByTaskId(taskId);
        if (task != null) {
            task.setStatus(ExportTask.TaskStatus.RUNNING);
            task.preUpdate();
            exportTaskMapper.updateById(task);
        }
    }
    
    /**
     * 更新任务进度
     * 使用 REQUIRES_NEW 确保在独立事务中执行，避免与流式查询的连接冲突
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void updateProgress(String taskId, long processedRows, long totalRows) {
        ExportTask task = exportTaskMapper.selectByTaskId(taskId);
        if (task != null) {
            task.setTotalRows(totalRows);
            task.setProcessedRows(processedRows);
            if (totalRows > 0) {
                int progress = (int) ((processedRows * 100) / totalRows);
                task.setProgress(progress);
            }
            task.preUpdate();
            exportTaskMapper.updateById(task);
            log.debug("更新任务进度: taskId={}, processedRows={}, totalRows={}, progress={}", 
                    taskId, processedRows, totalRows, task.getProgress());
        } else {
            log.warn("更新任务进度失败：任务不存在: taskId={}", taskId);
        }
    }
    
    /**
     * 更新任务为成功状态
     * 使用 REQUIRES_NEW 确保在独立事务中执行，避免与流式查询的连接冲突
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void updateToSuccess(String taskId, String filePath, String filename, long totalRows) {
        ExportTask task = exportTaskMapper.selectByTaskId(taskId);
        if (task != null) {
            task.setStatus(ExportTask.TaskStatus.SUCCESS);
            task.setProcessedRows(totalRows);
            task.setExportUrl(filePath);
            task.setFilename(filename);
            task.setExportName(filename);
            task.setProgress(100);
            task.preUpdate();
            exportTaskMapper.updateById(task);
            log.info("任务更新为成功: taskId={}, exportUrl={}", taskId, filePath);
        } else {
            log.warn("任务更新为成功失败，记录不存在: taskId={}", taskId);
        }
    }
    
    /**
     * 更新任务为失败状态
     * 使用 REQUIRES_NEW 确保在独立事务中执行，避免与流式查询的连接冲突
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void updateToFailed(String taskId, String errorMessage) {
        ExportTask task = exportTaskMapper.selectByTaskId(taskId);
        if (task != null) {
            task.setStatus(ExportTask.TaskStatus.FAILED);
            task.setErrorMessage(errorMessage);
            task.preUpdate();
            exportTaskMapper.updateById(task);
        }
    }
    
    /**
     * 更新任务为失败状态（无数据）
     * 使用 REQUIRES_NEW 确保在独立事务中执行，避免与流式查询的连接冲突
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void updateToFailedNoData(String taskId, String errorMessage) {
        ExportTask task = exportTaskMapper.selectByTaskId(taskId);
        if (task != null) {
            task.setStatus(ExportTask.TaskStatus.FAILED);
            task.setErrorMessage(errorMessage);
            task.setProgress(0);
            task.preUpdate();
            exportTaskMapper.updateById(task);
        }
    }
    
    /**
     * 分页条件查询导出任务
     * 
     * @param queryDTO 查询条件DTO
     * @return 分页结果
     */
    public PageData<ExportTask> pageQuery(ExportTaskQueryDTO queryDTO) {

        
        // 构建查询条件
        LambdaQueryWrapper<ExportTask> queryWrapper = new LambdaQueryWrapper<>();
        
        // 任务ID（模糊查询）
        if (StringUtils.hasText(queryDTO.getTaskId())) {
            queryWrapper.like(ExportTask::getTaskId, queryDTO.getTaskId());
        }
        
        // 操作人ID
        if (queryDTO.getOperatorEmployeeId() != null) {
            queryWrapper.eq(ExportTask::getOperatorEmployeeId, queryDTO.getOperatorEmployeeId());
        }
        
        // 菜单类型
        if (StringUtils.hasText(queryDTO.getMenuCode())) {
            queryWrapper.like(ExportTask::getMenuCode, queryDTO.getMenuCode());
        }
        
        // 任务状态
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(ExportTask::getStatus, queryDTO.getStatus());
        }
        
        // 文件名（模糊查询）
        if (StringUtils.hasText(queryDTO.getFilename())) {
            queryWrapper.like(ExportTask::getFilename, queryDTO.getFilename());
        }
        
        // 创建时间范围
        if (queryDTO.getCreatedAtStart() != null) {
            queryWrapper.ge(ExportTask::getCreatedAt, queryDTO.getCreatedAtStart());
        }
        if (queryDTO.getCreatedAtEnd() != null) {
            queryWrapper.le(ExportTask::getCreatedAt, queryDTO.getCreatedAtEnd());
        }
        
        // 更新时间范围
        if (queryDTO.getUpdatedAtStart() != null) {
            queryWrapper.ge(ExportTask::getUpdatedAt, queryDTO.getUpdatedAtStart());
        }
        if (queryDTO.getUpdatedAtEnd() != null) {
            queryWrapper.le(ExportTask::getUpdatedAt, queryDTO.getUpdatedAtEnd());
        }
        
        // 排序
        if (StringUtils.hasText(queryDTO.getOrderBy())) {
            boolean isAsc = "asc".equalsIgnoreCase(queryDTO.getOrderDirection());
            switch (queryDTO.getOrderBy().toLowerCase()) {
                case "created_at":
                    queryWrapper.orderBy(true, isAsc, ExportTask::getCreatedAt);
                    break;
                case "updated_at":
                    queryWrapper.orderBy(true, isAsc, ExportTask::getUpdatedAt);
                    break;
                case "status":
                    queryWrapper.orderBy(true, isAsc, ExportTask::getStatus);
                    break;
                default:
                    // 默认按创建时间倒序
                    queryWrapper.orderByDesc(ExportTask::getCreatedAt);
                    break;
            }
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc(ExportTask::getCreatedAt);
        }
        
        // 执行分页查询
        return PageInvoker.page(
                () -> exportTaskMapper.selectList(queryWrapper),
                queryDTO
        );
    }
}

