package cn.sdpjw.export.dao;

import cn.sdpjw.export.entity.ExportTask;
import cn.sdpjw.export.stub.dto.ExportRecordQuery;
import cn.sdpjw.export.stub.dto.ExportRecordVO;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 导出元数据 Mapper（固定元数据库）
 */
@DS("meta")
@Mapper
public interface ExportRecordMapper extends BaseMapper<ExportTask> {
    
    /**
     * 查询导出记录列表（不分页）
     * @param query 查询条件
     * @return 导出记录列表
     */
    List<ExportRecordVO> listRecords(@Param("query") ExportRecordQuery query);
}