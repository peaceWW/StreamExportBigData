package cn.sdpjw.export.dao.biz;

import cn.sdpjw.export.entity.ElectronicAccountDailyAvailableBalance;
import cn.sdpjw.export.stub.request.ElectronicBillFlowListExportRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.session.ResultHandler;

/**
 * @Description: 每日客户渠道可用余额 Mapper
 * @Author: songbaicheng
 * @Create: 2024/10/9 21:01
 **/
public interface ElectronicAccountDailyAvailableBalanceMapper extends BaseMapper<ElectronicAccountDailyAvailableBalance> {

    /**
     * 统计指定企业的电子交易账户数量
     */
    @ResultType(Long.class)
    Integer pageCount(ElectronicBillFlowListExportRequest request);

    /**
     * 流式查询指定企业的电子交易账户数据
     * ⚠️ 必须加 @Options(fetchSize = Integer.MIN_VALUE) 启用 MySQL 游标流式
     */
    @Options(fetchSize = Integer.MIN_VALUE)
    void pageList(ElectronicBillFlowListExportRequest request, ResultHandler<ElectronicAccountDailyAvailableBalance> handler);

}
