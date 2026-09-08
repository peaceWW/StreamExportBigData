package cn.sdpjw.export.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author 吴
 * @version 1.0
 */
@Data
@TableName(value = "bill_order", autoResultMap = true)
public class DemoRecord {
    private Long id;
    private String orderNo;
    private Integer orderStatus;
}
