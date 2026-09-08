package cn.sdpjw.export.stub.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 导出/查询菜单枚举
demo * 用于标识系统中各个菜单模块的功能类型及是否支持导出
 */
@Getter
@AllArgsConstructor
public enum MenuEnum {

    /**
     * 演示菜单（测试用）
     */
    DEMO("demo", "演示导出", true),

    /**
     * 电子交易账户菜单
     */
    ELECTRONIC_ACCOUNT("m_corp_manage_channel_list", "渠道列表-导出", true),

    /**
     * 三要素管理导出菜单
     */
    THREE_ELEMENT_MANAGE_EXPORT("m_risk_manage_three_element_manage", "三要素管理-导出", true),

    /**
     * 深度米/贝充值成功记录按日统计导出
     */
    MARGIN_RECHARGE_DAILY_REPORT("m_finance_margin_recharge_daily_report", "深度米贝充值日统计-导出", true),

    /**
     * 对账单导出菜单
     */
    ELECTRONIC_BILL_FLOW_EXPORT("m_electronic_bill_flow", "对账单-导出", true),

    /**
     * 奖励报表-每日统计导出
     */
    REWARD_DAILY_REPORT("m_finance_reward_daily_report", "奖励报表日统计-导出", true),

    /**
     * 赠送报表-每日统计导出
     */
    GIFT_DAILY_REPORT("m_finance_gift_daily_report", "赠送报表日统计-导出", true),

    /**
     * 兑换报表导出
     */
    MARKET_EXCHANGE_DAILY_REPORT("m_finance_market_exchange_daily_report", "兑换报表-导出", true),


    /**
     * 承接贝账户明细列表导出菜单
     */
    M_MARGIN_MANAGE_ACCOUNT_DETAIL_EXPORT("m_margin_manage_account_detail", "承接贝账户明细列表-导出", true),

    /**
     * 承接贝账户明细列表导出菜单
     */
    M_MARGIN_MANAGE_ACCOUNT_DETAIL_OFFLINE_EXPORT("m_margin_manage_account_detail_offline", "承接贝账户明细列表-导出", true),

    /**
     * 承接贝账户明细列表导出菜单
     */
    M_MARGIN_MANAGE_ACCOUNT_DETAIL_PLATFORM_OFFLINE_EXPORT("m_margin_manage_account_detail_platform_offline", "承接贝账户明细列表-导出", true);



    /**
     * 菜单编码
     * 用于唯一标识一个菜单模块，通常与前端路由或后端接口路径对应
     */
    private final String menuCode;

    /**
     * 菜单描述
     * 用于展示菜单的中文名称
     */
    private final String describe;

    /**
     * 是否支持导出
     * true-支持导出功能，false-仅支持查询
     */
    private final boolean exportable;

    /**
     * 根据菜单编码获取对应的枚举值
     *
     * @param menuCode 菜单编码
     * @return 对应的MenuEnum枚举值，未找到返回null
     */
    public static MenuEnum of(String menuCode) {
        if (menuCode == null) {
            return null;
        }
        for (MenuEnum item : values()) {
            if (item.menuCode.equals(menuCode)) {
                return item;
            }
        }
        return null;
    }
}