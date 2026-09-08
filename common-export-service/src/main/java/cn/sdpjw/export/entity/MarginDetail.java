package cn.sdpjw.export.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author 吴
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MarginDetail implements Serializable {

    /**
     * 企业名称
     */
    private String corpName;

    /**
     * 手机号码
     */
    private String masterMobile;

    /**
     * 收支类型
     */
    private Integer balanceType;

    /**
     * 类型
     */
    private Integer detailType;

    /**
     * 账户ID
     */
    private Long marginAccountId;

    /**
     * 账户类型
     */
    private Integer marginType;

    /**
     * 金额(贝)
     */
    private BigDecimal changeAmt;


    /**
     * 关联订单号
     */
    private String bizSerialId;

    /**
     * 备注
     */
    private String memo;

    /**
     * 对内备注
     */
    private String innerMemo;

    /**
     * 时间
     */
    private Long createTime;

    /**
     * 操作人
     */
    private String operationName;
}
