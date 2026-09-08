package cn.sdpjw.export.handler;

import lombok.Data;

/**
 * 断点续传上下文
 */
@Data
public class ExportCheckpoint {

    private Long processedRows;
    private String lastId;
    private Integer partNo;
    private String tempFilePath;
}
