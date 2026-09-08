package cn.sdpjw.export.dao;


import cn.sdpjw.export.config.LocalDateTimeTypeHandler;
import cn.sdpjw.export.config.TaskStatusTypeHandler;
import cn.sdpjw.export.entity.ExportTask;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * 导出任务Mapper
 */
@DS("meta")
@Mapper
public interface ExportTaskMapper extends BaseMapper<ExportTask> {
    /**
     * 根据任务ID查询
     */
    @Select("SELECT * FROM export_data_task WHERE task_id = #{taskId}")
    @Results({
        @Result(column = "status", property = "status", typeHandler = TaskStatusTypeHandler.class),
        @Result(column = "created_at", property = "createdAt", typeHandler = LocalDateTimeTypeHandler.class),
        @Result(column = "updated_at", property = "updatedAt", typeHandler = LocalDateTimeTypeHandler.class)
    })
    ExportTask selectByTaskId(@Param("taskId") String taskId);

}

