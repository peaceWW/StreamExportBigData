/**
 * Copyright (c) 2021 sdpjw Inc. All rights reserved.
 */

package cn.sdpjw.export.enums.margin;

import cn.sdpjw.common.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收支类型枚举
 */
@AllArgsConstructor
@Getter
public enum MarginTypeEnum {
    //ALL为了兼容冻结类型
    ALL(0, "全部账户", "全部账户"),
    RECHARGE(1, "充值贝", "充值贝"),
    GIVING(2, "赠送贝", "赠送贝"),
    REWARD(3, "活动奖励贝", "活动奖励"),
    WITHDRAW(4, "vip奖励贝", "提现奖励"),
    SEDIMENT(5, "通道奖励贝", "余额奖励");

    private final Integer index;

    private final String msg;

    /**
     * 平台赠送对应文本
     */
    private final String text;


    public static MarginTypeEnum fromIndex(Integer index) {
        for (MarginTypeEnum value : MarginTypeEnum.values()) {
            if (value.getIndex().equals(index)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 通过账号类型文本获取账号类型值
     * @param text
     * @return
     */
    public static MarginTypeEnum fromText(String text) {
        for (MarginTypeEnum value : MarginTypeEnum.values()) {
            if (value.getText().equals(text)) {
                return value;
            }
        }
        throw new BusinessException("账号类型文本不正确");
    }

}
