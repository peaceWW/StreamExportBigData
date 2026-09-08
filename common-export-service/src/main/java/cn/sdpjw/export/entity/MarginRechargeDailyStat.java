package cn.sdpjw.export.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 深度米/贝充值成功记录按日统计
 *
 * @author 吴
 * @version 1.0
 */
@Data
@ToString
@TableName("margin_recharge_daily_report")
public class MarginRechargeDailyStat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "stat_id", type = IdType.AUTO)
    private Long statId;

    /**
     * 充值完成时间(统计日期)
     */
    private LocalDate statDate;

    /**
     * 充值方式 0线下转账 1网关支付 2扫码支付
     */
    private Integer dictPayType;

    /**
     * 充值笔数
     */
    private Integer rechargeCount;

    /**
     * 充值金额
     */
    private BigDecimal totalRechargeAmt;

    /**
     * 手续费金额
     */
    private BigDecimal totalServiceAmt;

    /**
     * 平台到账金额
     */
    private BigDecimal platformArrivalAmt;

    /**
     * 客户到账充值贝
     */
    private BigDecimal arrivalRechargeAmt;

    /**
     * 客户到账赠送贝
     */
    private BigDecimal arrivalGivingAmt;

    /**
     * 客户总到账贝
     */
    private BigDecimal totalArrivalAmt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
