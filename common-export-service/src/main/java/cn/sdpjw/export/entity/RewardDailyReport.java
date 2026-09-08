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
 * 奖励报表-每日统计数据
 *
 * @author 吴
 * @version 1.0
 */
@Data
@ToString
@TableName("reward_daily_report")
public class RewardDailyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务发生时间（归集日期）
     */
    private LocalDate businessDate;

    /**
     * 当天发放奖励贝/米总额
     */
    private BigDecimal issuedReward;

    /**
     * 见证交易消耗的奖励贝/米
     */
    private BigDecimal witnessConsumed;

    /**
     * 兑换交易消耗的奖励贝/米
     */
    private BigDecimal exchangeConsumed;

    /**
     * 提现交易消耗的奖励贝/米
     */
    private BigDecimal withdrawConsumed;

    /**
     * 提现失败或撤销返还的奖励贝/米
     */
    private BigDecimal withdrawReturned;

    /**
     * 合计消耗奖励贝/米 (见证+兑换+提现-返还)
     */
    private BigDecimal totalConsumed;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    private LocalDateTime updateTime;
}
