package cn.sdpjw.export.dao.biz;

import cn.sdpjw.export.entity.MarginRechargeDailyStat;
import cn.sdpjw.export.stub.request.MarginRechargeDailyStatExportRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.session.ResultHandler;

/**
 * 深度米/贝充值成功记录按日统计 Mapper
 *
 * @author 吴
 * @version 1.0
 */
@Mapper
public interface MarginRechargeDailyStatMapper extends BaseMapper<MarginRechargeDailyStat> {

    /**
     * 统计符合条件的记录数
     */
    @ResultType(Long.class)
    Integer pageCount(MarginRechargeDailyStatExportRequest request);

    /**
     * 流式查询日统计数据
     * 必须加 @Options(fetchSize = Integer.MIN_VALUE) 启用 MySQL 游标流式
     */
    @Options(fetchSize = Integer.MIN_VALUE)
    void pageList(MarginRechargeDailyStatExportRequest request, ResultHandler<MarginRechargeDailyStat> handler);
}
