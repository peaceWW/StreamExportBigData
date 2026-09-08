package cn.sdpjw.export.stub.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据周期枚举
 */
@Getter
@AllArgsConstructor
public enum DataPeriodEnum {

    Date(1, "日"),
    Week(2, "周"),
    Month(3, "月");

    private final Integer period;
    private final String name;

    public static DataPeriodEnum getEnumByPeriod(Integer period) {
        for (DataPeriodEnum item : DataPeriodEnum.values()) {
            if (item.getPeriod().equals(period)) {
                return item;
            }
        }
        return null;
    }

}