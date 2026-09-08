package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.TraderCorpMemberThreeElementsManagerCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.TraderCorpMemberThreeElementsManagerMapper;
import cn.sdpjw.export.entity.TraderCorpMemberThreeElementsManager;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.enums.MenuEnum;
import cn.sdpjw.export.stub.request.ThreeElementsManagerExportRequest;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Consumer;

/**
 * @author: liuyuebai
 * @date: 2026/7/20 16:39
 * @description:
 */
@Component
public class TraderCorpMemberThreeElementsManagerService implements ExportHandler<ThreeElementsManagerExportRequest> {

    @Resource
    private TraderCorpMemberThreeElementsManagerMapper traderCorpMemberThreeElementsManagerMapper;
    @Resource
    private TraderCorpMemberThreeElementsManagerCsvFieldMapper fieldMapper;

    @Override
    public String menuCode() {
       return MenuEnum.THREE_ELEMENT_MANAGE_EXPORT.getMenuCode();
    }

    @Override
    public Class<ThreeElementsManagerExportRequest> queryType() {
        return ThreeElementsManagerExportRequest.class;
    }

    @Override
    public String fileNamePrefix(ThreeElementsManagerExportRequest request) {
        return "三要素管理";
    }

    @Override
    public String remotePath(ThreeElementsManagerExportRequest request) {
        return "exports/three_elements_manager_export";
    }

    @Override
    public CountProvider countProvider(ThreeElementsManagerExportRequest request, ExportCheckpoint checkpoint) {
        return () -> traderCorpMemberThreeElementsManagerMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(ThreeElementsManagerExportRequest request, ExportCheckpoint checkpoint) {
        return handler -> traderCorpMemberThreeElementsManagerMapper.pageList(request,(ResultHandler<TraderCorpMemberThreeElementsManager>) handler);
    }

    @Override
    public CsvFieldMapper<TraderCorpMemberThreeElementsManager> fieldMapper() {
        return fieldMapper;
    }
}
