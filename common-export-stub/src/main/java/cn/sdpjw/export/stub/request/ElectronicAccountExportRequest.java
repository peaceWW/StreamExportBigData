package cn.sdpjw.export.stub.request;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: liuyuebai
 * @date: 2026/7/6 19:43
 * @description:
 */
@Setter
@Getter
@ToString
public class ElectronicAccountExportRequest extends ExportRequest implements Serializable {

    private static final long serialVersionUID = -2784025195403921490L;
    /**
     * 虚户账号 =账户名称
     */
    private String accountName;

    /**
     * 支付渠道类型，1-华瑞银行、2-易宝
     */
    private Integer paymentChannel;

    /**
     * 支付渠道账号开通状态，0-未开通、1-开通中、2-开通成功、3-开通失败
     */
    private Integer accountStatus;

    /**
     * 签约状态，默认为0，0-未签约，1-签约中，2-签约成功，3-签约失败
     */
    private Integer signStatus;

    /**
     * 禁用状态，1-禁用、0-正常
     */
    private Integer banStatus;

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
     *
     */
    private String menuCode;

    /**
     * 渠道集合
     */
    private List<Integer> paymentChannelList;
}
