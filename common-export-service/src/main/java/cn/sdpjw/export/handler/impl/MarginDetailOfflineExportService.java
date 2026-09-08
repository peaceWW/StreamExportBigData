package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.MarginDetailOfflineCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.MarginDetailOfflineMapper;
import cn.sdpjw.export.entity.MarginDetail;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.enums.MenuEnum;
import cn.sdpjw.export.stub.request.MarginDetailRequestRequest;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 电子交易账户导出服务
 * @author 吴
 * @version 1.0
 */
@Service
public class MarginDetailOfflineExportService implements ExportHandler<MarginDetailRequestRequest> {


    @Autowired
    private MarginDetailOfflineMapper marginDetailOfflineMapper;

    @Autowired
    private MarginDetailOfflineCsvFieldMapper fieldMapper;

    @Override
    public String menuCode() {
        return MenuEnum.M_MARGIN_MANAGE_ACCOUNT_DETAIL_OFFLINE_EXPORT.getMenuCode();
    }

    @Override
    public Class<MarginDetailRequestRequest> queryType() {
        return MarginDetailRequestRequest.class;
    }

    @Override
    public String fileNamePrefix(MarginDetailRequestRequest request) {
        return "承接贝账户明细列表";
    }

    @Override
    public String remotePath(MarginDetailRequestRequest request) {
        return "exports/m_margin_manage_account_detail_offline";
    }

    @Override
    public CountProvider countProvider(MarginDetailRequestRequest request, ExportCheckpoint checkpoint) {
        return () -> marginDetailOfflineMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(MarginDetailRequestRequest request, ExportCheckpoint checkpoint) {
        return handler -> marginDetailOfflineMapper.pageList(request, (ResultHandler<MarginDetail>) handler);
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}