package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.MarginRechargeDailyStat;
import cn.sdpjw.export.enums.margin.DictPayTypeEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 深度米/贝充值日统计 CSV 字段映射器
 *
 * @author 吴
 * @version 1.0
 */
@Component
public class MarginRechargeDailyStatCsvFieldMapper implements CsvFieldMapper<MarginRechargeDailyStat> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String[] getHeaders() {
        return new String[]{
                "统计日期", "充值方式", "充值笔数", "充值金额", "手续费金额",
                "平台到账金额", "客户到账充值贝", "客户到账赠送贝", "客户总到账贝"
        };
    }

    @Override
    public Object[] mapToRow(MarginRechargeDailyStat record) {
        return new Object[]{
                formatDate(record.getStatDate()),
                getPayTypeDesc(record.getDictPayType()),
                record.getRechargeCount(),
                record.getTotalRechargeAmt(),
                record.getTotalServiceAmt(),
                record.getPlatformArrivalAmt(),
                record.getArrivalRechargeAmt(),
                record.getArrivalGivingAmt(),
                record.getTotalArrivalAmt()
        };
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    private String getPayTypeDesc(Integer dictPayType) {
        if (Objects.isNull(dictPayType)) {
            return "";
        }
        DictPayTypeEnum payTypeEnum = DictPayTypeEnum.byCode(dictPayType);
        return payTypeEnum == null ? "" : payTypeEnum.getDesc();
    }
}
