package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.ElectronicAccountCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.MarginDetailMapper;
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
public class MarginDetailPlatformOfflineExportService implements ExportHandler<MarginDetailRequestRequest> {


    @Autowired
    private MarginDetailMapper marginDetailMapper;

    @Autowired
    private ElectronicAccountCsvFieldMapper fieldMapper;

    @Override
    public String menuCode() {
        return MenuEnum.M_MARGIN_MANAGE_ACCOUNT_DETAIL_PLATFORM_OFFLINE_EXPORT.getMenuCode();
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
        return "exports/m_margin_manage_account_detail_platform_offline";
    }

    @Override
    public CountProvider countProvider(MarginDetailRequestRequest request, ExportCheckpoint checkpoint) {
        return () -> marginDetailMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(MarginDetailRequestRequest request, ExportCheckpoint checkpoint) {
        return handler -> marginDetailMapper.pageList(request, (ResultHandler<MarginDetail>) handler);
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}