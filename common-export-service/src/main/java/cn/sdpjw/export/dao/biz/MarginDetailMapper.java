package cn.sdpjw.export.dao.biz;

import cn.sdpjw.export.entity.ElectronicAccount;
import cn.sdpjw.export.entity.MarginDetail;
import cn.sdpjw.export.stub.request.ElectronicAccountExportRequest;
import cn.sdpjw.export.stub.request.MarginDetailRequestRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.session.ResultHandler;

/**
 * 电子交易账户Mapper
 * @author 吴
 * @version 1.0
 */
@Mapper
public interface MarginDetailMapper extends BaseMapper<MarginDetail> {

    /**
     * 统计指定企业的电子交易账户数量
     */
    @ResultType(Long.class)
    Integer pageCount(MarginDetailRequestRequest request);

    /**
     * 流式查询指定企业的电子交易账户数据
     * ⚠️ 必须加 @Options(fetchSize = Integer.MIN_VALUE) 启用 MySQL 游标流式
     */
    @Options(fetchSize = Integer.MIN_VALUE)
    void pageList(MarginDetailRequestRequest request, ResultHandler<MarginDetail> handler);


}