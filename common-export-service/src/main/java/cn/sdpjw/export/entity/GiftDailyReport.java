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
 * 赠送报表-每日统计数据
 *
 * @author 吴
 * @version 1.0
 */
@Data
@ToString
@TableName("gift_daily_report")
public class GiftDailyReport implements Serializable {

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
     * 当天发放赠送贝/米总额
     */
    private BigDecimal issuedGiftAmount;

    /**
     * 见证交易扣除的赠送贝/米
     */
    private BigDecimal witnessConsumedGift;

    /**
     * 到期失效被扣除的赠送贝/米
     */
    private BigDecimal expiredDeductedGift;

    /**
     * 总消耗赠送贝/米（见证消耗+到期扣除）
     */
    private BigDecimal totalConsumedGift;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    private LocalDateTime updateTime;
}
