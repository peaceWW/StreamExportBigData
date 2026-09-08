package cn.sdpjw.export.dao.biz;

import cn.sdpjw.export.entity.MarketExchangeDailyReport;
import cn.sdpjw.export.stub.request.MarketExchangeDailyReportExportRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.session.ResultHandler;

/**
 * 兑换报表 Mapper
 *
 * @author 吴
 * @version 1.0
 */
@Mapper
public interface MarketExchangeDailyReportMapper extends BaseMapper<MarketExchangeDailyReport> {

    /**
     * 统计符合条件的记录数
     */
    @ResultType(Long.class)
    Integer pageCount(MarketExchangeDailyReportExportRequest request);

    /**
     * 流式查询兑换报表数据
     * 必须加 @Options(fetchSize = Integer.MIN_VALUE) 启用 MySQL 游标流式
     */
    @Options(fetchSize = Integer.MIN_VALUE)
    void pageList(MarketExchangeDailyReportExportRequest request, ResultHandler<MarketExchangeDailyReport> handler);
}
