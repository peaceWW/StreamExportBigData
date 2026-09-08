package cn.sdpjw.export.stub.request;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 兑换报表导出请求
 *
 * @author 吴
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class MarketExchangeDailyReportExportRequest extends ExportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 兑换成功开始日期（含），格式 yyyy-MM-dd
     */
    private String startDate;

    /**
     * 兑换成功结束日期（含），格式 yyyy-MM-dd
     */
    private String endDate;

    /**
     * 兑换方式：0-对公兑换，1-对私兑换
     */
    private Integer exchangeWay;
}
