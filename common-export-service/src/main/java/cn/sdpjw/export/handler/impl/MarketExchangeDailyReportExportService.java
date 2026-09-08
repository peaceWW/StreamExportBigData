package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.MarketExchangeDailyReportCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.MarketExchangeDailyReportMapper;
import cn.sdpjw.export.entity.MarketExchangeDailyReport;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.enums.MenuEnum;
import cn.sdpjw.export.stub.request.MarketExchangeDailyReportExportRequest;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 兑换报表导出
 *
 * @author 吴
 * @version 1.0
 */
@Service
public class MarketExchangeDailyReportExportService implements ExportHandler<MarketExchangeDailyReportExportRequest> {

    @Autowired
    private MarketExchangeDailyReportMapper marketExchangeDailyReportMapper;

    @Autowired
    private MarketExchangeDailyReportCsvFieldMapper fieldMapper;

    @Override
    public String menuCode() {
        return MenuEnum.MARKET_EXCHANGE_DAILY_REPORT.getMenuCode();
    }

    @Override
    public Class<MarketExchangeDailyReportExportRequest> queryType() {
        return MarketExchangeDailyReportExportRequest.class;
    }

    @Override
    public String fileNamePrefix(MarketExchangeDailyReportExportRequest request) {
        return "承接贝兑换报表统计";
    }

    @Override
    public String remotePath(MarketExchangeDailyReportExportRequest request) {
        return "exports/market_exchange_daily_report";
    }

    @Override
    public CountProvider countProvider(MarketExchangeDailyReportExportRequest request, ExportCheckpoint checkpoint) {
        return () -> marketExchangeDailyReportMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(MarketExchangeDailyReportExportRequest request, ExportCheckpoint checkpoint) {
        return handler -> marketExchangeDailyReportMapper.pageList(request, (ResultHandler<MarketExchangeDailyReport>) handler);
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}
