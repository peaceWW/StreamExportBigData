/**
 * Copyright (c) 2021 sdpjw Inc. All rights reserved.
 */

package cn.sdpjw.export.enums.margin;

import lombok.Getter;

/**
 * 收支类型枚举
 */

@Getter
public enum BalanceTypeEnum  {
    INCOME(1, "收入"),
    EXPEND(2, "支出"),
    FREEZE(3, "冻结"),
    UNFREEZE(4, "解冻");
    private final Integer index;

    private final String text;

    BalanceTypeEnum(Integer i, String msg) {
        index = i;
        text = msg;
    }

    public static BalanceTypeEnum fromIndex(Integer index) {
        for (BalanceTypeEnum value : BalanceTypeEnum.values()) {
            if (value.getIndex().equals(index)) {
                return value;
            }
        }
        return null;
    }

    public static String translate (Integer index) {
        String text = "";
        BalanceTypeEnum balanceTypeEnum = fromIndex(index);
        if (null != balanceTypeEnum) {
            text = balanceTypeEnum.getText();
        }
        return text;
    }


    public Integer getIndex() {
        return index;
    }


    public String getText() {
        return text;
    }
}
