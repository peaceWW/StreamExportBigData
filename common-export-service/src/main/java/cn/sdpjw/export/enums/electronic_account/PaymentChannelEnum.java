package cn.sdpjw.export.enums.electronic_account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付渠道枚举
 *
 * @author 曾令辉LinghuiZeng
 * @date 2021-11-13 10:35
 */
@Getter
@AllArgsConstructor
public enum PaymentChannelEnum {

    /**
     * 支付渠道类型
     */
    HR_BANK(31, "华瑞银行", "华瑞银行"),
    YI_BAO(32, "智链通", "智链通"),
    HUI_YUAN(34, "智汇通", "汇元"),
    YI_FU_BAO(36, "智易通", "易付宝"),
    HUI_QI_FU(39, "智付汇元", "汇元"),
    ZHONG_QI_FU(40, "智付中金", "中金"),
    YI_QI_FU(41, "智付e宝", "易付宝"),
    SU_QI_FU(42, "智付苏商", "侯马付"),
    FU_MIN(43, "智e富", "富银企"),
    HUA_RUI(44, "智付华瑞", "智付华瑞"),
    ZHI_FU_JIE_SUAN(45, "智付结算", "江苏登保证金模式"),
    JIANG_SU_DENG(46, "江苏登", "江苏登虚户模式"),
    ;

    private final static int NEW_CHANNEL_START_VALUE = 31;

    /**
     * 渠道编码
     */
    private final Integer code;
    /**
     * 渠道名称
     */
    private final String name;
    /**
     * 渠道描述
     */
    private final String desc;

    /**
     * 实名成功后，支付渠道默认开通成功，需要签约的渠道
     * 未签约该模式允许持票方发布票据，不允许资金方接单
     */
    private final static List<Integer> ACCOUNT_SIGN_MODEL_LIST = new ArrayList<>(10);
    /**
     * 实名成功后，支付渠道默认签约成功，需要开通的渠道
     * 未开通时，该模式不允许持票方发布票据，允许资金方接单
     */
    private final static List<Integer> ACCOUNT_OPEN_MODEL_LIST = new ArrayList<>(2);
    private final static List<Integer> ACCOUNT_OPEN_MODEL_JJS_LIST = new ArrayList<>(3);
    /**
     * 虚户模式，实名后，需要开通的渠道，需要签约
     * 未签约、未开通时，该模式不允许持票方发布票据，不允许资金方接单
     */
    private final static List<Integer> VIRTUAL_ACCOUNT_MODEL_LIST = new ArrayList<>(10);

    private final static List<Integer> DISABLE_CHANNEL_LIST = new ArrayList<>(8);
    /****
     * 回单下载列表
     */
    private final static List<Integer> DOWN_ELECTRONIC_RECRIPT = new ArrayList<>(8);

    /****
     * 提现免费配置列表
     */
    private final static List<Integer> WITHDRAWAL_ACCOUNT_MODEL_LIST = new ArrayList<>(3);

    /**
     * 经纪商
     */
    private final static List<Integer> ACCOUNT_SIGN_MODEL_JJS_LIST = new ArrayList<>();
    private final static List<Integer> VIRTUAL_ACCOUNT_MODEL_JJS_LIST = new ArrayList<>();
    private final static List<Integer> ACCOUNT_MODEL_JJS_LIST = new ArrayList<>();

    static {
        DISABLE_CHANNEL_LIST.add(HR_BANK.getCode());
        DISABLE_CHANNEL_LIST.add(YI_BAO.getCode());
        DISABLE_CHANNEL_LIST.add(HUI_YUAN.getCode());
        DISABLE_CHANNEL_LIST.add(YI_FU_BAO.getCode());

        ACCOUNT_SIGN_MODEL_LIST.add(SU_QI_FU.code);
        ACCOUNT_SIGN_MODEL_LIST.add(HUA_RUI.code);
        ACCOUNT_SIGN_MODEL_LIST.add(ZHI_FU_JIE_SUAN.code);

        VIRTUAL_ACCOUNT_MODEL_LIST.add(HUI_QI_FU.code);
        VIRTUAL_ACCOUNT_MODEL_LIST.add(YI_QI_FU.code);
        VIRTUAL_ACCOUNT_MODEL_LIST.add(FU_MIN.code);
        VIRTUAL_ACCOUNT_MODEL_LIST.add(JIANG_SU_DENG.code);
        VIRTUAL_ACCOUNT_MODEL_LIST.add(ZHONG_QI_FU.getCode());


        //回单下载
        DOWN_ELECTRONIC_RECRIPT.add(HUI_QI_FU.getCode());
        DOWN_ELECTRONIC_RECRIPT.add(YI_QI_FU.getCode());
        DOWN_ELECTRONIC_RECRIPT.add(FU_MIN.getCode());
        DOWN_ELECTRONIC_RECRIPT.add(SU_QI_FU.getCode());
        DOWN_ELECTRONIC_RECRIPT.add(JIANG_SU_DENG.getCode());
        DOWN_ELECTRONIC_RECRIPT.add(ZHI_FU_JIE_SUAN.getCode());
        DOWN_ELECTRONIC_RECRIPT.add(ZHONG_QI_FU.getCode());

        //是否配置免费提现
        WITHDRAWAL_ACCOUNT_MODEL_LIST.add(YI_QI_FU.getCode());
        WITHDRAWAL_ACCOUNT_MODEL_LIST.add(HUI_QI_FU.getCode());
        WITHDRAWAL_ACCOUNT_MODEL_LIST.add(FU_MIN.getCode());
        WITHDRAWAL_ACCOUNT_MODEL_LIST.add(JIANG_SU_DENG.getCode());
        WITHDRAWAL_ACCOUNT_MODEL_LIST.add(ZHONG_QI_FU.getCode());

        //经纪商
        VIRTUAL_ACCOUNT_MODEL_JJS_LIST.add(HUI_QI_FU.code);

        ACCOUNT_MODEL_JJS_LIST.addAll(ACCOUNT_SIGN_MODEL_JJS_LIST);
        ACCOUNT_MODEL_JJS_LIST.addAll(VIRTUAL_ACCOUNT_MODEL_JJS_LIST);
    }

    /**
     * 根据渠道编码获取枚举值
     *
     * @param code 渠道编码
     * @return 枚举值
     */
    public static PaymentChannelEnum byCode(Integer code) {
        for (PaymentChannelEnum item : PaymentChannelEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据渠道描述获取枚举值
     *
     * @param name 渠道描述
     * @return 枚举值
     */
    public static PaymentChannelEnum byName(String name) {
        for (PaymentChannelEnum item : PaymentChannelEnum.values()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static List<Integer> channelsByJJS(){
        return ACCOUNT_MODEL_JJS_LIST;
    }

    /**
     * 是否智链通渠道
     *
     * @param code 渠道编码
     * @return true or false
     */
    public static boolean isYb(Integer code) {
        return false;
    }

    /**
     * 是否智汇通渠道
     *
     * @param code 渠道编码
     * @return true or false
     */
    public static boolean isHuiYuan(Integer code) {
        return false;
    }

    public static boolean isHuiYuanChannel(Integer code) {
        return HUI_QI_FU.code.equals(code);
    }

    /**
     * 是否智易通渠道
     *
     * @param code 渠道编码
     * @return true or false
     */
    public static boolean isYiFuBao(Integer code) {
        return false;
    }

    public static boolean isZhongQiFu(Integer code) {
        return ZHONG_QI_FU.code.equals(code);
    }

    /**
     * 是否汇企付渠道
     *
     * @param code 渠道编码
     * @return true or false
     */
    public static boolean isHuiQiFu(Integer code) {
        return HUI_QI_FU.code.equals(code);
    }

    public static boolean isYiQiFu(Integer code) {
        return YI_QI_FU.code.equals(code);
    }

    /**
     * 是否苏企付渠道
     *
     * @param code 渠道编码
     * @return true or false
     */
    public static boolean isSuQiFu(Integer code) {
        return SU_QI_FU.code.equals(code);
    }


    /**
     * 需要用户签约渠道，实名成功后，account_status=2,sign_status = 0
     */
    public static boolean isAccountSignModel(Integer code) {
        return ACCOUNT_SIGN_MODEL_LIST.contains(code);
    }
    /**
     * 经纪商
     */
    public static List<Integer> getAccountSignModelJJS() {
        return ACCOUNT_SIGN_MODEL_JJS_LIST;
    }

    /**
     * 实名成功后是否需要用户开通渠道 account_status=0,sign_status = 2
     */
    public static boolean isAccountOpenModel(Integer code) {
        return ACCOUNT_OPEN_MODEL_LIST.contains(code);
    }
    public static List<Integer> getAccountOpenModelJJS() {
        return ACCOUNT_OPEN_MODEL_JJS_LIST;
    }

    /**
     * 是否虚拟账户模式
     * account_status=0,sign_status = 0
     *
     * @param code 渠道编码
     * @return true or false
     */
    public static boolean isVirtualAccountModel(Integer code) {
        return VIRTUAL_ACCOUNT_MODEL_LIST.contains(code);
    }

    /**
     * 实名成功 初始化电子账户时 需要默认未开通的渠道
     */
    public static boolean isInitAccountClose(Integer code) {
        return Arrays.asList().contains(code);
    }

    /**
     * 获取虚拟账户模式渠道列表f
     *
     * @return 渠道编码列表
     */
    public static List<Integer> getVirtualAccountList() {
        return VIRTUAL_ACCOUNT_MODEL_LIST;
    }

    public static Map<String, PaymentChannelEnum> getPaymentChannelMap() {
        return Arrays.stream(PaymentChannelEnum.values()).collect(Collectors.toMap(PaymentChannelEnum::getName, v -> v));
    }

    public static boolean isOldChannel(Integer code) {
        return code < NEW_CHANNEL_START_VALUE;
    }

    public static boolean isDisableChannel(Integer code) {
        return DISABLE_CHANNEL_LIST.contains(code);
    }

    /**
     * 是否富银企渠道
     *
     * @param code 渠道编码
     * @return true or false
     */
    public static boolean isFuMin(Integer code) {
        return FU_MIN.code.equals(code);
    }

    /**
     * 智付结算-江苏登保证金模式
     */
    public static boolean isZFJS(Integer code) {
        return ZHI_FU_JIE_SUAN.code.equals(code);
    }

    public static boolean isJSD(Integer code) {
        return JIANG_SU_DENG.code.equals(code);
    }

    /**
     * 根据渠道编码判断是否回单下载
     *
     * @param code 渠道编码
     * @return boolean
     */
    public static boolean isDownElectronicReceipt(Integer code) {
        return DOWN_ELECTRONIC_RECRIPT.contains(code);
    }

    /**
     * 后管导出冻结金额
     * @Param [channel]
     * @Return boolean
     */
    public static boolean isFrozenAmountExport(Integer channel) {
        return FU_MIN.code.equals(channel) || HUI_QI_FU.code.equals(channel) || JIANG_SU_DENG.code.equals(channel) || ZHI_FU_JIE_SUAN.code.equals(channel);
    }

    /**
     * 后管导出可用金额
     * @Param [channel]
     * @Return boolean
     */
    public static boolean isUsableAmountExport(Integer channel) {
        return FU_MIN.code.equals(channel) || JIANG_SU_DENG.code.equals(channel) || ZHI_FU_JIE_SUAN.code.equals(channel);
    }

    /**
     * 提现免费配置列表
     * @Param [channel]
     * @Return boolean
     */
    public static boolean isWithdrawalAccountList(Integer code) {
        return WITHDRAWAL_ACCOUNT_MODEL_LIST.contains(code);
    }

    /**
     * 是否经纪商渠道
     * @param code
     * @return
     */
    public static boolean isBrokerChannel(Integer code) {
        return ACCOUNT_MODEL_JJS_LIST.contains(code);
    }

    public static List<PaymentChannelEnum> getDisableTimeSupportedChannels() {
        return Arrays.asList(PaymentChannelEnum.YI_QI_FU,
                PaymentChannelEnum.HUI_QI_FU,
                PaymentChannelEnum.SU_QI_FU,
                PaymentChannelEnum.FU_MIN,
                PaymentChannelEnum.JIANG_SU_DENG,
                PaymentChannelEnum.ZHI_FU_JIE_SUAN,
                PaymentChannelEnum.ZHONG_QI_FU);
    }
}
