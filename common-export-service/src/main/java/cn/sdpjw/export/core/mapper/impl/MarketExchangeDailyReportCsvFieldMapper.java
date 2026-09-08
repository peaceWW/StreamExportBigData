package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.MarketExchangeDailyReport;
import cn.sdpjw.export.enums.exchange.ExchangeTypeEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 兑换报表 CSV 字段映射器
 *
 * @author 吴
 * @version 1.0
 */
@Component
public class MarketExchangeDailyReportCsvFieldMapper implements CsvFieldMapper<MarketExchangeDailyReport> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String[] getHeaders() {
        return new String[]{
                "兑换成功日期", "兑换方式", "兑换奖励贝数值", "扣除的服务费金额",
                "实际打款到账金额"
        };
    }

    @Override
    public Object[] mapToRow(MarketExchangeDailyReport record) {
        return new Object[]{
                formatDate(record.getExchangeDate()),
                getExchangeTypeDesc(record.getExchangeWay()),
                record.getRewardAmount(),
                record.getServiceFee(),
                record.getReceivedAmount(),
                record.getOrderNo(),
                record.getCompanyName()
        };
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    private String getExchangeTypeDesc(Integer exchangeType) {
        if (Objects.isNull(exchangeType)) {
            return "";
        }
        ExchangeTypeEnum typeEnum = ExchangeTypeEnum.byCode(exchangeType);
        return typeEnum == null ? "" : typeEnum.getDesc();
    }
}
