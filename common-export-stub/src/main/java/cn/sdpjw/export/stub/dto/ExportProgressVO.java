package cn.sdpjw.export.stub.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 导出进度
 */
@Data
public class ExportProgressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    private Integer status;
    private String statusDesc;
    private Integer progress;
    private Long processedRows;
    private Long totalRows;
    private String exportUrl;
    private String errorMessage;
}
