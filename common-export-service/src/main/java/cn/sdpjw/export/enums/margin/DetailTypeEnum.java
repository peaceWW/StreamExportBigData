/**
 * Copyright (c) 2021 sdpjw Inc. All rights reserved.
 */

package cn.sdpjw.export.enums.margin;

import cn.sdpjw.coin.stub.enums.AppPlatEnum;
import cn.sdpjw.coin.stub.enums.EnableEnum;
import cn.sdpjw.common.base.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum DetailTypeEnum {
    RECHARGE(1, "充值","充值",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK), EnableEnum.ENABLE,null),
    INTEGRAL(2, "积分兑换","积分兑换",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    PLATFORM(3, "交易对手赔付","违约赔付收入",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    UNFREEZE(4, "承接贝解冻","保证金解冻",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    RETURN(5, "兑换失败退回","兑换失败退回",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    REDPACKET(6, "红包兑换","红包兑换",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    PLATFORM_DEDUCTION(7, "平台扣除","平台扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    SDM_DEDUCTION(8, "承接贝扣除","违约赔付扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    SERVICE_FEE_FREEZE(9, "承接贝冻结","保证金冻结",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    TRANSFER_INTO(10, "划转转入","划转转入",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    TRANSFER_OUT(11, "划转转出","划转转出",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,10),
    BARGAINING_FREEZE(12, "议价冻结","议价冻结",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    BARGAINING_UNFREEZE(13, "议价解冻","议价解冻",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    RECHARGE_GIVING(14, "充值赠送","充值赠送",usedInAppPlats(AppPlatEnum.ERP_FRONT,  AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    ACTIVITY_REWARD(15, "活动奖励","活动奖励",usedInAppPlats(AppPlatEnum.ERP_FRONT,  AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    ACTIVITY_REDUCE(16, "活动扣除","活动扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    CANCEL_REDUCE(17, "违约奖励扣除","违约奖励扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    PLAT_GIVING(18, "平台赠送","平台赠送",usedInAppPlats(AppPlatEnum.ERP_FRONT,  AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    ACTIVITY_GIVING_REDUCE(19,"平台赠送扣除","平台赠送扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    SEDIMENT(20,"通道奖励","通道奖励",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    WITHDRAW(21,"VIP奖励","VIP奖励",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    PLATFORM_DEDUCTION_RETURN(22,"平台扣除退回","平台扣除退回",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    VOUCHER_DEDUCT(23,"交易凭证扣除","交易凭证扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    VOUCHER_PLAT(24,"交易凭证","平台收取",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    PLATFORM_FEE_FREEZE(25,"服务费冻结","服务费冻结",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    PLATFORM_FEE_UNFREEZE(26,"服务费解冻","服务费解冻",usedInAppPlats(AppPlatEnum.ERP_FRONT,AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,null),
    PLATFORM_FEE_REDUCE(27,"服务费扣除","服务费扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT,AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,28),
    PLATFORM_FEE_INCOME(28,"平台服务费收入","服务费收入",usedInAppPlats(AppPlatEnum.ERP_BACK),EnableEnum.ENABLE,null),
    BROKER_RATE_GAIN(29,"通道奖励分润","通道奖励分润收入",usedInAppPlats(AppPlatEnum.JJS_ERP_FRONT),EnableEnum.DISENABLE,null),
    CHARGING_FEE(30,"充值手续费","充值手续费",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.ENABLE,40),
    BROKER_FEE_FREEZE(31,"接单冻结","渠道手续费冻结",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    BROKER_FEE_UNFREEZE(32,"订单失败解冻","渠道手续费解冻",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    BROKER_FEE_REDUCE(33,"手续费扣除","渠道手续费扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    BROKER_FEE_INCOME(34,"手续费收入","渠道手续费收入",usedInAppPlats(AppPlatEnum.ERP_BACK),EnableEnum.DISENABLE,null),
    ATTENTION_SERVICE_FEE(35,"关注企业的服务费","关注服务费",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    BROKER_SERVICE_FEE_FREEZE(36,"服务费用冻结","服务费用冻结",usedInAppPlats(AppPlatEnum.JJS_ERP_FRONT),EnableEnum.DISENABLE,null),
    BROKER_SERVICE_FEE_UNFREEZE(37,"服务费用解冻","服务费用解冻",usedInAppPlats(AppPlatEnum.JJS_ERP_FRONT),EnableEnum.DISENABLE,null),
    BROKER_SERVICE_FEE_REDUCE(38,"服务费用扣除","服务费用扣除",usedInAppPlats(AppPlatEnum.JJS_ERP_FRONT),EnableEnum.DISENABLE,null),
    BROKER_SERVICE_FEE_INCOME(39,"服务费用收入","服务费用收入",usedInAppPlats(AppPlatEnum.ERP_FRONT, AppPlatEnum.JJS_ERP_BACK),EnableEnum.DISENABLE,null),
    CHARGING_FEE_INCOME(40,"充值手续费收入","充值手续费收入",usedInAppPlats(AppPlatEnum.ERP_BACK),EnableEnum.ENABLE,null),
    PURCHASE_PACKAGE_REDUCE(41,"购买查看票方联系方式套餐","联系方式套餐扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT),EnableEnum.ENABLE,42),
    PURCHASE_PACKAGE_INCOME(42,"购买联系方式套餐收入","联系方式套餐收入",usedInAppPlats(AppPlatEnum.ERP_BACK),EnableEnum.ENABLE,null),
    PURCHASE_QUERY_PRICE_REDUCE(43,"购买查询价格套餐","查询价格套餐扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT),EnableEnum.ENABLE,44),
    PURCHASE_QUERY_PRICE_INCOME(44,"购买查询价格套餐收入","查询价格套餐收入",usedInAppPlats(AppPlatEnum.ERP_BACK),EnableEnum.ENABLE,null),
    WITHDRAW_SERVICE_FEE_FREEZE(45,"提现手续费冻结","手续费冻结",usedInAppPlats(AppPlatEnum.ERP_FRONT,AppPlatEnum.JJS_ERP_FRONT),EnableEnum.ENABLE,null),
    WITHDRAW_SERVICE_FEE_UNFREEZE(46,"提现手续费解冻","手续费解冻",usedInAppPlats(AppPlatEnum.ERP_FRONT,AppPlatEnum.JJS_ERP_FRONT),EnableEnum.ENABLE,null),
    PLATFORM_WITHDRAW_SERVICE_FEE_REDUCE(47,"提现手续费扣除","手续费扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT,AppPlatEnum.JJS_ERP_FRONT),EnableEnum.ENABLE,48),
    PLATFORM_WITHDRAW_SERVICE_FEE_INCOME(48,"提现手续费收入","手续费收入",usedInAppPlats(AppPlatEnum.ERP_BACK),EnableEnum.ENABLE,null),
    WITHDRAW_SERVICE_FEE_REBACK(49,"提现手续费退回","手续费退回",usedInAppPlats(AppPlatEnum.ERP_FRONT,AppPlatEnum.JJS_ERP_FRONT),EnableEnum.ENABLE,null),
    VALID_GIVING_DEDUCTION(51,"赠送贝到期扣除","到期扣除",usedInAppPlats(AppPlatEnum.ERP_FRONT,AppPlatEnum.JJS_ERP_FRONT),EnableEnum.ENABLE,null),
    ;
    private final Integer index;
    /**
     * 明细类型-生成备注用
     */
    private final String text;
    /**
     * 明细类型描述-前后台下拉列表展示用
     */
    private final String desc;

    /**
     * 0-禁用  1-启用
     */
    private final Integer enable;

    /**
     * 平台户的index
     */
    private final Integer platIndex;

    /**
     * 应用平台，具体到各个应用的前后台
     * 1-承接erp前台  2-企承云erp前台  3-企承云erp后台 4-承接erp后台
     */
    private final List<Integer> appPlats;

    public static String translate (Integer index) {
        String text = "";
        DetailTypeEnum detailTypeEnum = fromIndex(index);
        if (null != detailTypeEnum) {
            text = detailTypeEnum.getDesc();
        }
        return text;
    }

    DetailTypeEnum(Integer i, String text, String des, List<Integer> appPlats, EnableEnum enable, Integer platIndex) {
        this.index = i;
        this.text = text;
        this.desc = des;
        this.appPlats = appPlats;
        this.enable = enable.getIndex();
        this.platIndex = platIndex;
    }

    /**
     * 获取冻结枚举
     */
    public static List<Integer> findFrozen() {
        return Arrays.asList(SERVICE_FEE_FREEZE.index, BARGAINING_FREEZE.index, PLATFORM_FEE_FREEZE.index,
                BROKER_FEE_FREEZE.index,BROKER_SERVICE_FEE_FREEZE.index,WITHDRAW_SERVICE_FEE_FREEZE.index);
    }

    /**
     * 获取解冻枚举
     */
    public static List<Integer> findUnfreeze() {
        return Arrays.asList(UNFREEZE.index, BARGAINING_UNFREEZE.index, PLATFORM_FEE_UNFREEZE.index, BROKER_FEE_UNFREEZE.index,
                WITHDRAW_SERVICE_FEE_UNFREEZE.index);
    }

    /**
     * 获取冻结和解冻枚举
     */
    public static List<Integer> findFrozenAndUnfreeze() {
        List<Integer> list = new ArrayList<>(findFrozen());
        list.addAll(new ArrayList<>(findUnfreeze()));
        return list;
    }

    /**
     * 充值、赠送、奖励共有的枚举
     */
    public static List<Integer> findCommon() {
        return Arrays.asList( SERVICE_FEE_FREEZE.index,UNFREEZE.index, PLATFORM_DEDUCTION.index, SDM_DEDUCTION.index,
                TRANSFER_OUT.index,BARGAINING_FREEZE.index,BARGAINING_UNFREEZE.index,PLATFORM_DEDUCTION_RETURN.index,
                VOUCHER_DEDUCT.index,PLATFORM_FEE_FREEZE.index, PLATFORM_FEE_UNFREEZE.index, ATTENTION_SERVICE_FEE.index,
                BROKER_SERVICE_FEE_FREEZE.index,BROKER_SERVICE_FEE_UNFREEZE.index,BROKER_SERVICE_FEE_REDUCE.index,
                BROKER_SERVICE_FEE_INCOME.index,PURCHASE_PACKAGE_REDUCE.index,PURCHASE_PACKAGE_INCOME.index,
                PURCHASE_QUERY_PRICE_REDUCE.index,PURCHASE_QUERY_PRICE_INCOME.index,WITHDRAW_SERVICE_FEE_FREEZE.index,
                WITHDRAW_SERVICE_FEE_UNFREEZE.index);
    }

    /**
     * 获取充值枚举
     */
    public static List<Integer> findRecharges() {
        List<Integer> rechargeList = Lists.newArrayList();
        rechargeList.addAll(findCommon());
        rechargeList.addAll(Arrays.asList(RECHARGE.index,PLATFORM.index,RETURN.index,REDPACKET.index,TRANSFER_INTO.index,
                PLATFORM_FEE_REDUCE.index, CHARGING_FEE.index,PLATFORM_WITHDRAW_SERVICE_FEE_REDUCE.index));
        return rechargeList;
    }

    /**
     * 获取赠送枚举
     */
    public static List<Integer> findGivings() {
        List<Integer> givingList = Lists.newArrayList();
        givingList.addAll(findCommon());
        givingList.addAll(Arrays.asList(RECHARGE_GIVING.index, PLAT_GIVING.index, ACTIVITY_GIVING_REDUCE.index));
        return givingList;
    }

    /**
     * 获取奖励枚举
     */
    public static List<Integer> findRewards() {
        List<Integer> rewardList = Lists.newArrayList();
        rewardList.addAll(findCommon());
        rewardList.addAll(Arrays.asList(RETURN.index,REDPACKET.index, ACTIVITY_REWARD.index,ACTIVITY_REDUCE.index,
                SEDIMENT.index, WITHDRAW.index, PLATFORM_FEE_REDUCE.index,PLATFORM_WITHDRAW_SERVICE_FEE_REDUCE.index));
        return rewardList;
    }

    /**
     * 与订单相关的detailType集合,供前后台展示订单号
     */
    public static List<Integer> detailTypesAssociatedOrder() {
        List<Integer> rewardList = Lists.newArrayList();
        rewardList.addAll(Arrays.asList(PLATFORM.index,UNFREEZE.index, SDM_DEDUCTION.index,SERVICE_FEE_FREEZE.index,
                ACTIVITY_REWARD.index, VOUCHER_DEDUCT.index,VOUCHER_PLAT.index, PLATFORM_FEE_FREEZE.index,PLATFORM_FEE_UNFREEZE.index,PLATFORM_FEE_REDUCE.index,PLATFORM_FEE_INCOME.index,
                BROKER_FEE_FREEZE.index,BROKER_FEE_UNFREEZE.index,BROKER_FEE_REDUCE.index,BROKER_FEE_INCOME.index,BROKER_SERVICE_FEE_FREEZE.index,
                BROKER_SERVICE_FEE_UNFREEZE.index,BROKER_SERVICE_FEE_REDUCE.index,BROKER_SERVICE_FEE_INCOME.index,PURCHASE_PACKAGE_REDUCE.index,PURCHASE_QUERY_PRICE_REDUCE.index,
                WITHDRAW_SERVICE_FEE_FREEZE.index,WITHDRAW_SERVICE_FEE_UNFREEZE.index,PLATFORM_WITHDRAW_SERVICE_FEE_REDUCE.index,PLATFORM_WITHDRAW_SERVICE_FEE_INCOME.index));
        return rewardList;
    }


    public static DetailTypeEnum fromIndex(Integer index) {
        for (DetailTypeEnum value : DetailTypeEnum.values()) {
            if (value.getIndex().equals(index)) {
                return value;
            }
        }
        return null;
    }

    private static List<Integer> usedInAppPlats(AppPlatEnum ... args) {
        List<Integer> usedInAppPlats = new ArrayList<>();
        ArrayList<AppPlatEnum> appPlatEnums = new ArrayList<>(Arrays.asList(args));
        for (AppPlatEnum appPlatEnum : appPlatEnums) {
            usedInAppPlats.add(appPlatEnum.getIndex());
        }
        return usedInAppPlats;
    }

    /**
     * 通过类型描述获取账户明细类型
     * @param desc
     * @return
     */
    public static Integer fromDesc(String desc) {
        for (DetailTypeEnum value : DetailTypeEnum.values()) {
            if (value.getDesc().equals(desc)) {
                return value.index;
            }
        }
        throw new BusinessException("账户明细描述不正确");
    }

}
