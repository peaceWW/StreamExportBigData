package cn.sdpjw.export.core.callback;

/**
 * 进度回调接口
 * 用于实时更新导出进度
 */
@FunctionalInterface
public interface ProgressCallback {
    /**
     * 更新进度
     * 
     * @param taskId 任务ID
     * @param processedRows 已处理行数
     * @param totalRows 总行数
     */
    void onProgress(String taskId, long processedRows, long totalRows);
}

