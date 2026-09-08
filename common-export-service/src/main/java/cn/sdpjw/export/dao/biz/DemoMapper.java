package cn.sdpjw.export.dao.biz;

import cn.sdpjw.export.entity.DemoRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.ResultHandler;

/**
 * 易联账户流水Mapper
 * @author 吴
 * @version 1.0
 */
@Mapper
public interface DemoMapper extends BaseMapper<DemoRecord> {

    /**
     * 流式查询指定企业 & 时间范围内的账务流水（按创建时间倒序）
     * ⚠️ 必须加 @Options(fetchSize = Integer.MIN_VALUE) 启用 MySQL 游标流式
     */
    @Select("<script>" +
            "SELECT id, order_no, order_status " +
            "FROM bill_order " +
            "</script>")
    @Options(fetchSize = Integer.MIN_VALUE)
    @ResultType(DemoRecord.class)
    void selectByCorpAndTimeRange(
            @Param("traderCorpId") Integer traderCorpId,
            ResultHandler<DemoRecord> handler);

    @Select("<script>" +
            "SELECT count(*) " +
            "FROM bill_order " +
            "</script>")
    @ResultType(Long.class)
    long countByCorpAndTime(Integer traderCorpId);
}
