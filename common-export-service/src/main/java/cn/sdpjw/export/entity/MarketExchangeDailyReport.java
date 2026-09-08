package cn.sdpjw.export.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 兑换报表数据
 *
 * @author 吴
 * @version 1.0
 */
@Data
@ToString
@TableName("market_exchange_daily_report")
public class MarketExchangeDailyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 兑换成功日期
     */
    private LocalDate exchangeDate;

    /**
     * 兑换方式：0-对公兑换，1-对私兑换
     */
    private Integer exchangeWay;

    /**
     * 兑换奖励贝/米数值
     */
    private BigDecimal rewardAmount;

    /**
     * 扣除的服务费金额
     */
    private BigDecimal serviceFee;

    /**
     * 实际打款到账金额
     */
    private BigDecimal receivedAmount;

    /**
     * 关联的订单号或流水号
     */
    private String orderNo;

    /**
     * 企业名称
     */
    private String companyName;
}
