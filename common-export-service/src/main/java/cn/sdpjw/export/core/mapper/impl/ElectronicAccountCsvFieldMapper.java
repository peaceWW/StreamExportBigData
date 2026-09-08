package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.ElectronicAccount;
import cn.sdpjw.export.enums.electronic_account.AccountSignStatusEnum;
import cn.sdpjw.export.enums.electronic_account.PaymentAccountStatusEnum;
import cn.sdpjw.export.enums.electronic_account.PaymentChannelEnum;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 电子交易账户CSV字段映射器
 * @author 吴
 * @version 1.0
 */
@Component
public class ElectronicAccountCsvFieldMapper implements CsvFieldMapper<ElectronicAccount> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String[] getHeaders() {
        return new String[]{
                "企业名称", "交易渠道", "签约状态", "开通状态", "更新时间",
                "失败原因", "是否展示渠道", "是否支持发布", "是否支持接单",
        };
    }

    @Override
    public Object[] mapToRow(ElectronicAccount record) {
        return new Object[]{
                record.getAccountName(),
                getPaymentChannelDesc(record.getPaymentChannel()),
                getSignStatusDesc(record.getSignStatus()),
                getAccountStatusDesc(record.getAccountStatus()),
                formatDateTime(record.getUpdatedAt()),
                record.getAccountFailReason(),
                getBanStatusDesc(record.getBanStatus()),
                getTurnOnStatusDesc(record.getTurnOnStatus()),
                getSignDisableStatusDesc(record.getSignDisableStatus()),
        };
    }

    private String getPaymentChannelDesc(Integer paymentChannel) {
        if (Objects.isNull(paymentChannel)){
            return "";
        }
        PaymentChannelEnum paymentChannelEnum = PaymentChannelEnum.byCode(paymentChannel);
        if (Objects.isNull(paymentChannelEnum)) return "";
        return paymentChannelEnum.getName();
    }

    private String getSignStatusDesc(Integer signStatus) {
        if (signStatus == null) return "";
        AccountSignStatusEnum accountSignStatusEnum = AccountSignStatusEnum.fromBy(signStatus);
        if (null == accountSignStatusEnum){
            return "";
        }
        return accountSignStatusEnum.getDesc();
    }

    private String getAccountStatusDesc(Integer accountStatus) {
        if (accountStatus == null) return "";
        PaymentAccountStatusEnum paymentAccountStatusEnum = PaymentAccountStatusEnum.byCode(accountStatus);
        if (null == paymentAccountStatusEnum){
            return "";
        }
        return paymentAccountStatusEnum.getDesc();
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private String getBanStatusDesc(Integer banStatus) {
        if (banStatus == null) return "";
        return banStatus == 0 ? "开启" : "关闭";
    }

    private String getSignDisableStatusDesc(Integer signDisableStatus) {
        if (signDisableStatus == null) return "";
        return signDisableStatus == 0 ? "开启" : "关闭";
    }

    private String getTurnOnStatusDesc(Integer turnOnStatus) {
        if (turnOnStatus == null) return "";
        return turnOnStatus == 0 ? "开启" : "关闭";
    }
}