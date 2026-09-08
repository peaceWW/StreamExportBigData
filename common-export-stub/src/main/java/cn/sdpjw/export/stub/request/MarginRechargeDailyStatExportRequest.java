package cn.sdpjw.export.stub.request;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 深度米/贝充值成功记录按日统计导出请求
 *
 * @author 吴
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class MarginRechargeDailyStatExportRequest extends ExportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计开始日期（含），格式 yyyy-MM-dd
     */
    private String startDate;

    /**
     * 统计结束日期（含），格式 yyyy-MM-dd
     */
    private String endDate;

    /**
     * 充值方式：0线下转账 1网关支付 2扫码支付
     */
    private Integer dictPayType;
}
