package cn.sdpjw.export.stub.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 同步导出接口返回结果。
 * <p>
 * 实际可能以同步完成或降级异步两种模式返回，通过 {@link #syncMode} / {@link #degraded} 区分。
 */
@Data
public class ExportSyncResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String MODE_SYNC = "SYNC";
    public static final String MODE_ASYNC = "ASYNC";

    /**
     * 任务 ID
     */
    private String taskId;

    /**
     * 实际执行模式：{@link #MODE_SYNC} / {@link #MODE_ASYNC}
     */
    private String syncMode;

    /**
     * true 表示调用方请求同步，因数据量超过阈值已降级为异步
     */
    private Boolean degraded;

    /**
     * 任务状态码（与 ExportTask.TaskStatus.code 一致）
     */
    private Integer status;

    /**
     * 任务状态描述
     */
    private String statusDesc;

    /**
     * 导出文件下载地址（同步成功时有值；异步降级时通常为空）
     */
    private String exportUrl;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件格式
     */
    private String fileFormat;

    /**
     * 已处理行数
     */
    private Long processedRows;

    /**
     * 总行数（含前置 count 结果）
     */
    private Long totalRows;

    /**
     * 进度百分比
     */
    private Integer progress;

    /**
     * 失败原因
     */
    private String errorMessage;

    /**
     * 提示信息（如降级说明）
     */
    private String message;
}
