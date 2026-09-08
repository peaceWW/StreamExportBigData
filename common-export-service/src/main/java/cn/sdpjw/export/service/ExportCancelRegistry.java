package cn.sdpjw.export.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

/**
 * 导出任务取消标记
 */
@Component
public class ExportCancelRegistry {

    private final ConcurrentHashMap<String, AtomicBoolean> cancelFlags = new ConcurrentHashMap<>();

    public void register(String taskId) {
        cancelFlags.put(taskId, new AtomicBoolean(false));
    }

    public void cancel(String taskId) {
        AtomicBoolean flag = cancelFlags.get(taskId);
        if (flag != null) {
            flag.set(true);
        }
    }

    public boolean isCancelled(String taskId) {
        AtomicBoolean flag = cancelFlags.get(taskId);
        return flag != null && flag.get();
    }

    public void clear(String taskId) {
        cancelFlags.remove(taskId);
    }
}
