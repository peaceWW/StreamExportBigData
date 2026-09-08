package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.GiftDailyReportCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.GiftDailyReportMapper;
import cn.sdpjw.export.entity.GiftDailyReport;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.enums.MenuEnum;
import cn.sdpjw.export.stub.request.GiftDailyReportExportRequest;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 赠送报表-每日统计导出
 *
 * @author 吴
 * @version 1.0
 */
@Service
public class GiftDailyReportExportService implements ExportHandler<GiftDailyReportExportRequest> {

    @Autowired
    private GiftDailyReportMapper giftDailyReportMapper;

    @Autowired
    private GiftDailyReportCsvFieldMapper fieldMapper;

    @Override
    public String menuCode() {
        return MenuEnum.GIFT_DAILY_REPORT.getMenuCode();
    }

    @Override
    public Class<GiftDailyReportExportRequest> queryType() {
        return GiftDailyReportExportRequest.class;
    }

    @Override
    public String fileNamePrefix(GiftDailyReportExportRequest request) {
        return "承接贝赠送报表统计";
    }

    @Override
    public String remotePath(GiftDailyReportExportRequest request) {
        return "exports/gift_daily_report";
    }

    @Override
    public CountProvider countProvider(GiftDailyReportExportRequest request, ExportCheckpoint checkpoint) {
        return () -> giftDailyReportMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(GiftDailyReportExportRequest request, ExportCheckpoint checkpoint) {
        return handler -> giftDailyReportMapper.pageList(request, (ResultHandler<GiftDailyReport>) handler);
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}
