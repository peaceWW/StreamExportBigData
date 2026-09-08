package cn.sdpjw.export.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 电子交易账户表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-11-05
 */
@Data
@Accessors(chain = true)
@TableName("electronic_account")
@EqualsAndHashCode(callSuper = false)
public class ElectronicAccount implements Serializable {


    private static final long serialVersionUID = -1786601550102447849L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    /**
     * 企业id
     */
    private Integer traderCorpId;

    /**
     * 支付渠道类型，1-华瑞银行、2-易宝
     */
    private Integer paymentChannel;

    /**
     * 第三方 客户编号
     * 金运通：客户编号
     */
    private String thirdCustomerId;


    /**
     * 汇元 子商户id
     * 子商户登录账号 =>经办人邮箱
     * 易付宝 进件号
     */
    private String thirdSubAccount;

    /**
     * 签约渠道账号
     * 汇元:第三方 子商户id
     * 金运通：基本户
     */
    private String accountNo;

    /**
     * 虚户账号 =账户名称
     */
    private String accountName;

    /**
     * 支付渠道账号开通状态，0-未开通、1-开通中、2-开通成功、3-开通失败
     */
    private Integer accountStatus;

    /**
     * 渠道账号开通失败原因
     */
    private String accountFailReason;

    /**
     * 渠道账号开通成功时间
     */
    private LocalDateTime accountSuccessTime;

    /**
     * 签约流水号
     */
    private String signRequestNo;

    /**
     * 发起签约时间
     */
    private LocalDateTime initiateSignTime;

    /**
     * 签约状态，默认为0，0-未签约，1-签约中，2-签约成功，3-签约失败
     */
    private Integer signStatus;

    /**
     * 签约节点 0-未提交，1-审核中，2-协议待签署，3-业务开通中，4-申请已完成，5-申请已驳回
     */
    private Integer signingNode;

    /**
     * 签约成功时间
     */
    private LocalDateTime signSuccessTime;

    /**
     * 签约成功时间
     */
    private LocalDateTime auditSuccessTime;

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
     * 是否存量用户签约 0-不是，1-是
     */
    private Integer existSignFlag;

    /**
     * 用户是否已经注销 0-正常，1-注销
     */
    private Integer logoutFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;

    /**
     * 第三方收款账号
     */
    private String thirdReceiveAccount;

    /**
     * 客户经理id
     */
    private Integer customerManagerEmployeeId;

    /**
     * 客户经理名称
     */
    private String customerManagerEmployeeName;

    /**
     * 客户经理所属部门id
     */
    private Integer customerManagerDepartmentId;

    /**
     * 客户经理所属部门名称
     */
    private String customerManagerDepartmentName;

    /**
     * 签约节点变更时间
     */
    private LocalDateTime nodeUpdateTime;

    /**
     * 第三方账户名称
     */
    private String thirdAccountName;
}
