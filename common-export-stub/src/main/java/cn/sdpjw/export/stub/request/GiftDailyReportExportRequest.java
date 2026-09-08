package cn.sdpjw.export.stub.request;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 赠送报表-每日统计导出请求
 *
 * @author 吴
 * @version 1.0
 */
@Setter
@Getter
@ToString
public class GiftDailyReportExportRequest extends ExportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务发生开始日期（含），格式 yyyy-MM-dd
     */
    private String startDate;

    /**
     * 业务发生结束日期（含），格式 yyyy-MM-dd
     */
    private String endDate;
}
