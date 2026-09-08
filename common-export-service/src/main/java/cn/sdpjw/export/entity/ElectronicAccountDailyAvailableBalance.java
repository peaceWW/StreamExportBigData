package cn.sdpjw.export.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Description: 每日客户渠道可用余额
 * @Author: songbaicheng
 * @Create: 2024/10/9 20:51
 **/
@Data
@ToString
@Accessors(chain = true)
@TableName("electronic_account_daily_available_balance")
public class ElectronicAccountDailyAvailableBalance implements Serializable {

    private static final long serialVersionUID = 8083010090381423885L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 企业id
     */
    private Integer traderCorpId;

    /**
     * 企业名称
     */
    private String traderCorpName;

    /**
     * 支付渠道类型
     */
    private Integer paymentChannel;

    /**
     * 商户号
     */
    private String accountNo;

    /**
     * 可用余额查询日期
     */
    private LocalDate queryDate;

    /**
     * 冻结金额(元)
     */
    private BigDecimal frozenAmount;

    /**
     * 总余额(元)
     */
    private BigDecimal availableBalanceAmt;

    /**
     * 可用余额(元)
     */
    private BigDecimal usableAmount;

    /**
     * 执行时间
     */
    private LocalDateTime executionTime;

    /**
     * 失败标志
     */
    private Integer failureFlag;

    /**
     * 失败原因
     */
    private String failureReason;

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
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;

    /**
     * 备注
     */
    private String remark;

    /**
     * 经纪商id
     */
    private Integer brokerId;

    /**
     * 平台类型 6-承接 7-承载
     */
    private Integer registerType;

    /**
     * 智e富 限制描述
     */
    private String restrictionDesc;
}
