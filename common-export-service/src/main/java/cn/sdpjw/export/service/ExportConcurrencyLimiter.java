package cn.sdpjw.export.service;

/**
 * 导出并发限制
 */
public interface ExportConcurrencyLimiter {

    void acquire() throws InterruptedException;

    void release();
}
