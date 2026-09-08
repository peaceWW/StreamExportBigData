package cn.sdpjw.export.dto;

import cn.sdpjw.common.base.page.PageRequest;
import cn.sdpjw.export.entity.ExportTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 导出任务查询DTO
 * 用于接收分页查询参数和查询条件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportTaskQueryDTO extends PageRequest implements Serializable {
    

    
    /**
     * 任务ID（模糊查询）
     */
    private String taskId;
    
    /**
     * 操作人ID
     */
    private Integer operatorEmployeeId;
    
    /**
     * 菜单类型
     */
    private String menuCode;
    
    /**
     * 任务状态
     */
    private ExportTask.TaskStatus status;
    
    /**
     * 文件名（模糊查询）
     */
    private String filename;
    
    /**
     * 创建时间起始（包含）
     */
    private LocalDateTime createdAtStart;
    
    /**
     * 创建时间结束（包含）
     */
    private LocalDateTime createdAtEnd;
    
    /**
     * 更新时间起始（包含）
     */
    private LocalDateTime updatedAtStart;
    
    /**
     * 更新时间结束（包含）
     */
    private LocalDateTime updatedAtEnd;
    
    /**
     * 排序字段（如：created_at）
     */
    private String orderBy;
    
    /**
     * 排序方式（asc/desc）
     */
    private String orderDirection = "desc";
}
