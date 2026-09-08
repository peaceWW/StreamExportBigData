package cn.sdpjw.export.stub.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 导出记录视图
 */
@Data
public class ExportRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    private String menuCode;
    private String menuDescribe;
    private Integer operatorEmployeeId;
    private String operatorName;
    private String subscriberApp;
    private String dataSourceKey;
    private String queryParams;
    private String exportUrl;
    private String exportName;
    private String fileFormat;
    private Long processedRows;
    private Long totalRows;
    private Integer progress;
    private Integer status;
    private String statusDesc;
    private String errorMessage;
    private LocalDateTime receivedAt;
    private LocalDateTime exportTime;
    private LocalDateTime createdAt;
}
