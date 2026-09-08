package cn.sdpjw.export.core.mapper.impl;

import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.entity.ElectronicAccountDailyAvailableBalance;
import cn.sdpjw.export.enums.electronic_account.PaymentChannelEnum;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * 电子交易账户日终余额CSV字段映射器
 * 表头与行数据按渠道动态裁剪：冻结金额、可用余额仅部分渠道导出
 *
 * @author 吴
 * @version 1.0
 */
@Component
public class ElectronicAccountDailyAvailableBalanceFieldMapper implements CsvFieldMapper<ElectronicAccountDailyAvailableBalance> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String FIELD_FROZEN_AMOUNT = "frozenAmount";
    private static final String FIELD_USABLE_AMOUNT = "usableAmount";

    /**
     * 全量导出列，顺序即最终表头顺序；被 constructExcludeColumns 排除的列不会出现在结果中
     */
    private static final List<ExportColumn> ALL_COLUMNS = Arrays.asList(
            new ExportColumn("queryDate", "更新日期", record -> formatDate(record.getQueryDate())),
            new ExportColumn("traderCorpName", "企业名称", ElectronicAccountDailyAvailableBalance::getTraderCorpName),
            new ExportColumn("registerType", "平台类型", record -> getRegisterTypeDesc(record.getRegisterType())),
            new ExportColumn("accountNo", "汇元账户ID", ElectronicAccountDailyAvailableBalance::getAccountNo),
            new ExportColumn("availableBalanceAmt", "日终余额(元)", ElectronicAccountDailyAvailableBalance::getAvailableBalanceAmt),
            new ExportColumn(FIELD_USABLE_AMOUNT, "可用余额(元)", ElectronicAccountDailyAvailableBalance::getUsableAmount),
            new ExportColumn(FIELD_FROZEN_AMOUNT, "冻结金额(元)", ElectronicAccountDailyAvailableBalance::getFrozenAmount),
            new ExportColumn("paymentChannel", "支付渠道", record -> getPaymentChannelDesc(record.getPaymentChannel())),
            new ExportColumn("restrictionDesc", "账户限制描述", ElectronicAccountDailyAvailableBalance::getRestrictionDesc),
            new ExportColumn("customerManagerEmployeeName", "客户经理", ElectronicAccountDailyAvailableBalance::getCustomerManagerEmployeeName),
            new ExportColumn("customerManagerDepartmentName", "客拓所属组别", ElectronicAccountDailyAvailableBalance::getCustomerManagerDepartmentName)
    );

    private Integer channel;

    @Override
    public String[] getHeaders() {
        return resolveVisibleColumns().stream()
                .map(ExportColumn::header)
                .toArray(String[]::new);
    }

    @Override
    public Object[] mapToRow(ElectronicAccountDailyAvailableBalance record) {
        return resolveVisibleColumns().stream()
                .map(column -> column.extract(record))
                .toArray();
    }

    /**
     * 按当前渠道排除字段后，得到实际导出列（表头与行数据共用，保证列序一致）
     */
    private List<ExportColumn> resolveVisibleColumns() {
        Set<String> excludeColumns = constructExcludeColumns(getChannel());
        List<ExportColumn> visibleColumns = new ArrayList<>(ALL_COLUMNS.size());
        for (ExportColumn column : ALL_COLUMNS) {
            if (!excludeColumns.contains(column.field())) {
                visibleColumns.add(column);
            }
        }
        return visibleColumns;
    }

    /**
     * 动态构建排除字段列表
     */
    private Set<String> constructExcludeColumns(Integer channel) {
        Set<String> excludeColumns = new HashSet<>();
        // 智e富、智付汇元、江苏登、智付结算导出冻结金额，其余渠道排除
        boolean needExportFrozenAmount = PaymentChannelEnum.isFrozenAmountExport(channel);
        if (!needExportFrozenAmount) {
            excludeColumns.add(FIELD_FROZEN_AMOUNT);
        }
        // 仅智e富导出可用余额，其余渠道排除
        boolean needExportUsableAmount = PaymentChannelEnum.isFuMin(channel);
        if (!needExportUsableAmount) {
            excludeColumns.add(FIELD_USABLE_AMOUNT);
        }
        return excludeColumns;
    }

    private static String getPaymentChannelDesc(Integer paymentChannel) {
        if (Objects.isNull(paymentChannel)) {
            return "";
        }
        PaymentChannelEnum paymentChannelEnum = PaymentChannelEnum.byCode(paymentChannel);
        if (Objects.isNull(paymentChannelEnum)) {
            return "";
        }
        return paymentChannelEnum.getName();
    }

    /**
     * 平台类型 6-承接 7-承载
     */
    private static String getRegisterTypeDesc(Integer registerType) {
        if (registerType == null) {
            return "";
        }
        if (registerType == 6) {
            return "承接";
        }
        if (registerType == 7) {
            return "承载";
        }
        return String.valueOf(registerType);
    }

    private static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    private static final class ExportColumn {
        private final String field;
        private final String header;
        private final Function<ElectronicAccountDailyAvailableBalance, Object> extractor;

        private ExportColumn(String field, String header,
                             Function<ElectronicAccountDailyAvailableBalance, Object> extractor) {
            this.field = field;
            this.header = header;
            this.extractor = extractor;
        }

        private String field() {
            return field;
        }

        private String header() {
            return header;
        }

        private Object extract(ElectronicAccountDailyAvailableBalance record) {
            return extractor.apply(record);
        }
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }
}
