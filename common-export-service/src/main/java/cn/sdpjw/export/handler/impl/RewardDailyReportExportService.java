package cn.sdpjw.export.handler.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.mapper.impl.RewardDailyReportCsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.dao.biz.RewardDailyReportMapper;
import cn.sdpjw.export.entity.RewardDailyReport;
import cn.sdpjw.export.handler.ExportCheckpoint;
import cn.sdpjw.export.handler.ExportHandler;
import cn.sdpjw.export.stub.enums.MenuEnum;
import cn.sdpjw.export.stub.request.RewardDailyReportExportRequest;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * 奖励报表-每日统计导出
 *
 * @author 吴
 * @version 1.0
 */
@Service
public class RewardDailyReportExportService implements ExportHandler<RewardDailyReportExportRequest> {

    @Autowired
    private RewardDailyReportMapper rewardDailyReportMapper;

    @Autowired
    private RewardDailyReportCsvFieldMapper fieldMapper;

    @Override
    public String menuCode() {
        return MenuEnum.REWARD_DAILY_REPORT.getMenuCode();
    }

    @Override
    public Class<RewardDailyReportExportRequest> queryType() {
        return RewardDailyReportExportRequest.class;
    }

    @Override
    public String fileNamePrefix(RewardDailyReportExportRequest request) {
        return "承接贝奖励报表统计";
    }

    @Override
    public String remotePath(RewardDailyReportExportRequest request) {
        return "exports/reward_daily_report";
    }

    @Override
    public CountProvider countProvider(RewardDailyReportExportRequest request, ExportCheckpoint checkpoint) {
        return () -> rewardDailyReportMapper.pageCount(request);
    }

    @Override
    public Consumer<ResultHandler<?>> streamProvider(RewardDailyReportExportRequest request, ExportCheckpoint checkpoint) {
        return handler -> rewardDailyReportMapper.pageList(request, (ResultHandler<RewardDailyReport>) handler);
    }

    @Override
    public CsvFieldMapper<?> fieldMapper() {
        return fieldMapper;
    }
}
