package cn.sdpjw.export.stub.dto.query;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: liuyuebai
 * @date: 2026/7/3 17:44
 * @description:
 */
@Data
@ToString
public class ElectronicAccountExportDTO implements Serializable {
    private static final long serialVersionUID = 8181310288479956466L;


    /**
     * 支付渠道类型，1-华瑞银行、2-易宝
     */
    private Integer paymentChannel;

    /**
     * 虚户账号 =账户名称
     */
    private String accountName;

    /**
     * 签约状态，默认为0，0-未签约，1-签约中，2-签约成功，3-签约失败
     */
    private Integer signStatus;

    /**
     * 支付渠道账号开通状态，0-未开通、1-开通中、2-开通成功、3-开通失败
     */
    private Integer accountStatus;

    /**
     * 渠道账号开通失败原因
     */
    private String accountFailReason;

    /**
     * 平台侧控制
     * 签约渠道禁用 0-正常，1-禁用。
     * 和banStatus区别是，banStatus=1时，页面不显示该渠道
     * signDisableStatus=1时，显示签约状态禁用
     */
    private Integer signDisableStatus;

    /**
     * 平台侧控制
     *交易通道默认为开启状态，当开关关闭时，该渠道默认不支持发布
     * 开启状态 0-开启，1-关闭。
     */
    private Integer turnOnStatus;

    /**
     * 第三方渠道状态，0-正常，1-禁用
     */
    private Integer accountThirdStatus;

    /**
     * 禁用状态，1-禁用、0-正常
     */
    private Integer banStatus;

    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;

}
