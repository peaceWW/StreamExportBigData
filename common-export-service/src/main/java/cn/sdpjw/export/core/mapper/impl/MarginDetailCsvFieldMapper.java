package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.coin.stub.enums.DetailTypeEnum;
import cn.sdpjw.coin.stub.enums.MarginTypeEnum;
import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.MarginDetail;
import cn.sdpjw.export.enums.margin.BalanceTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
/**
 * 电子交易账户CSV字段映射器
 * @author 吴
 * @version 1.0
 */
@Component
public class MarginDetailCsvFieldMapper implements CsvFieldMapper<MarginDetail> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String[] getHeaders() {
        return new String[]{
                "企业名称", "手机号码", "收支类型", "类型", "账户类型",
                "金额(贝)", "关联订单号", "对客备注", "对内备注",
                "时间", "操作人"
        };
    }

    @Override
    public Object[] mapToRow(MarginDetail record) {
        return new Object[]{
                record.getCorpName(),
                maskMobile(record.getMasterMobile()),
                BalanceTypeEnum.translate(record.getBalanceType()),
                DetailTypeEnum.fromIndex(record.getDetailType()).getDesc(),
                MarginTypeEnum.fromIndex(record.getMarginType()).getText(),
                record.getChangeAmt(),
                record.getBizSerialId(),
                record.getMemo(),
                record.getInnerMemo(),
                formatDateTime(record.getCreateTime()),
                record.getOperationName()
        };
    }
    /**
     * 手机号脱敏：正则不匹配时不得回写明文。
     */
    private String maskMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return "";
        }
        String value = mobile.trim();
        if (value.contains("*")) {
            return value;
        }
        if (value.matches("\\d{11}")) {
            return value.substring(0, 3) + "****" + value.substring(7);
        }
        String digits = value.replaceAll("\\D", "");
        if (digits.length() >= 7) {
            return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
        }
        return "****";
    }

    private String formatDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.of("Asia/Shanghai") // 强制使用东八区
        );
        return dateTime.format(DATE_TIME_FORMATTER);
    }


}