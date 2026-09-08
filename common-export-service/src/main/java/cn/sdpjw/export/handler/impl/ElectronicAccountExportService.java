package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.ElectronicAccountCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.ElectronicAccountMapper;
import cn.sdpjw.export.entity.ElectronicAccount;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.enums.MenuEnum;
import cn.sdpjw.export.stub.request.ElectronicAccountExportRequest;
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
public class ElectronicAccountExportService implements ExportHandler<ElectronicAccountExportRequest> {


    @Autowired
    private ElectronicAccountMapper electronicAccountMapper;

    @Autowired
    private ElectronicAccountCsvFieldMapper fieldMapper;

    @Override
    public String menuCode() {
        return MenuEnum.ELECTRONIC_ACCOUNT.getMenuCode();
    }

    @Override
    public Class<ElectronicAccountExportRequest> queryType() {
        return ElectronicAccountExportRequest.class;
    }

    @Override
    public String fileNamePrefix(ElectronicAccountExportRequest request) {
        return "客户渠道列表";
    }

    @Override
    public String remotePath(ElectronicAccountExportRequest request) {
        return "exports/electronic_account";
    }

    @Override
    public CountProvider countProvider(ElectronicAccountExportRequest request, ExportCheckpoint checkpoint) {
        return () -> electronicAccountMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(ElectronicAccountExportRequest request, ExportCheckpoint checkpoint) {
        return handler -> electronicAccountMapper.pageList(request, (ResultHandler<ElectronicAccount>) handler);
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}