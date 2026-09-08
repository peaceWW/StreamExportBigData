package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.RewardDailyReport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 奖励报表-每日统计 CSV 字段映射器
 *
 * @author 吴
 * @version 1.0
 */
@Component
public class RewardDailyReportCsvFieldMapper implements CsvFieldMapper<RewardDailyReport> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String[] getHeaders() {
        return new String[]{
                "业务发生时间", "当天发放奖励贝总额", "见证交易消耗的奖励贝",
                "兑换交易消耗的奖励贝", "提现交易消耗的奖励贝",
                "提现失败或撤销返还的奖励贝", "合计消耗奖励贝"
        };
    }

    @Override
    public Object[] mapToRow(RewardDailyReport record) {
        return new Object[]{
                formatDate(record.getBusinessDate()),
                record.getIssuedReward(),
                record.getWitnessConsumed(),
                record.getExchangeConsumed(),
                record.getWithdrawConsumed(),
                record.getWithdrawReturned(),
                record.getTotalConsumed()
        };
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
}
