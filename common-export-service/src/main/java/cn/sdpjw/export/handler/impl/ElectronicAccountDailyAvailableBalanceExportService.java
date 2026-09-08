package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.ElectronicAccountDailyAvailableBalanceFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.ElectronicAccountDailyAvailableBalanceMapper;
import cn.sdpjw.export.entity.ElectronicAccountDailyAvailableBalance;
import cn.sdpjw.export.enums.electronic_account.PaymentChannelEnum;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.enums.MenuEnum;
import cn.sdpjw.export.stub.request.ElectronicBillFlowListExportRequest;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 对账单流水导出服务
 * @author 吴
 * @version 1.0
 */
@Service
public class ElectronicAccountDailyAvailableBalanceExportService implements ExportHandler<ElectronicBillFlowListExportRequest> {


    @Autowired
    private ElectronicAccountDailyAvailableBalanceMapper electronicAccountDailyAvailableBalanceMapper;

    @Autowired
    private ElectronicAccountDailyAvailableBalanceFieldMapper fieldMapper;



    @Override
    public String menuCode() {
        return MenuEnum.ELECTRONIC_BILL_FLOW_EXPORT.getMenuCode();
    }

    @Override
    public Class<ElectronicBillFlowListExportRequest> queryType() {
        return ElectronicBillFlowListExportRequest.class;
    }

    @Override
    public String fileNamePrefix(ElectronicBillFlowListExportRequest request) {
        return PaymentChannelEnum.byCode(request.getChannel()).getName() +"日终余额";
    }

    @Override
    public String remotePath(ElectronicBillFlowListExportRequest request) {
        return "exports/electronic_bill_flow_list";
    }

    @Override
    public CountProvider countProvider(ElectronicBillFlowListExportRequest request, ExportCheckpoint checkpoint) {
        fieldMapper.setChannel(request.getChannel());
        return () -> electronicAccountDailyAvailableBalanceMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(ElectronicBillFlowListExportRequest request, ExportCheckpoint checkpoint) {
        return handler -> electronicAccountDailyAvailableBalanceMapper.pageList(
                request,
                (ResultHandler<ElectronicAccountDailyAvailableBalance>) handler
        );
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}