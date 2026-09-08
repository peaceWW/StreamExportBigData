package cn.sdpjw.export.dao.biz;

import cn.sdpjw.export.entity.ElectronicAccount;
import cn.sdpjw.export.entity.TraderCorpMemberThreeElementsManager;
import cn.sdpjw.export.stub.request.ThreeElementsManagerExportRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.session.ResultHandler;

/**
 * @author: liuyuebai
 * @date: 2026/7/20 16:55
 * @description:
 */
@Mapper
public interface TraderCorpMemberThreeElementsManagerMapper extends BaseMapper<TraderCorpMemberThreeElementsManager> {

    /**
     * 统计指定企业的电子交易账户数量
     */
    @ResultType(Long.class)
    Integer pageCount(ThreeElementsManagerExportRequest request);

    /**
     * 流式查询指定企业的电子交易账户数据
     * ⚠️ 必须加 @Options(fetchSize = Integer.MIN_VALUE) 启用 MySQL 游标流式
     */
    @Options(fetchSize = Integer.MIN_VALUE)
    void pageList(ThreeElementsManagerExportRequest request, ResultHandler<TraderCorpMemberThreeElementsManager> handler);

}
