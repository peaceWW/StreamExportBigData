package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.MarginRechargeDailyStatCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.MarginRechargeDailyStatMapper;
import cn.sdpjw.export.entity.MarginRechargeDailyStat;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.enums.MenuEnum;
import cn.sdpjw.export.stub.request.MarginRechargeDailyStatExportRequest;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 深度米/贝充值成功记录按日统计导出
 *
 * @author 吴
 * @version 1.0
 */
@Service
public class MarginRechargeDailyStatExportService implements ExportHandler<MarginRechargeDailyStatExportRequest> {

    @Autowired
    private MarginRechargeDailyStatMapper marginRechargeDailyStatMapper;

    @Autowired
    private MarginRechargeDailyStatCsvFieldMapper fieldMapper;

    @Override
    public String menuCode() {
        return MenuEnum.MARGIN_RECHARGE_DAILY_REPORT.getMenuCode();
    }

    @Override
    public Class<MarginRechargeDailyStatExportRequest> queryType() {
        return MarginRechargeDailyStatExportRequest.class;
    }

    @Override
    public String fileNamePrefix(MarginRechargeDailyStatExportRequest request) {
        return "承接贝充值报表统计";
    }

    @Override
    public String remotePath(MarginRechargeDailyStatExportRequest request) {
        return "exports/margin_recharge_daily_report";
    }

    @Override
    public CountProvider countProvider(MarginRechargeDailyStatExportRequest request, ExportCheckpoint checkpoint) {
        return () -> marginRechargeDailyStatMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(MarginRechargeDailyStatExportRequest request, ExportCheckpoint checkpoint) {
        return handler -> marginRechargeDailyStatMapper.pageList(request, (ResultHandler<MarginRechargeDailyStat>) handler);
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}
