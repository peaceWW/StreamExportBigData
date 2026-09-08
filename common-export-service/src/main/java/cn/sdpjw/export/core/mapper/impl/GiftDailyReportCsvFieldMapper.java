package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.GiftDailyReport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 赠送报表-每日统计 CSV 字段映射器
 *
 * @author 吴
 * @version 1.0
 */
@Component
public class GiftDailyReportCsvFieldMapper implements CsvFieldMapper<GiftDailyReport> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String[] getHeaders() {
        return new String[]{
                "业务发生时间", "当天发放赠送贝总额", "见证交易扣除的赠送贝",
                "到期失效被扣除的赠送贝", "总消耗赠送贝"
        };
    }

    @Override
    public Object[] mapToRow(GiftDailyReport record) {
        return new Object[]{
                formatDate(record.getBusinessDate()),
                record.getIssuedGiftAmount(),
                record.getWitnessConsumedGift(),
                record.getExpiredDeductedGift(),
                record.getTotalConsumedGift()
        };
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
}
