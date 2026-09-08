package cn.sdpjw.export.service;

import cn.sdpjw.export.config.ExportProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
public class ExportConcurrencyLimiterImpl implements ExportConcurrencyLimiter {

    private final Semaphore semaphore;

    public ExportConcurrencyLimiterImpl(ExportProperties exportProperties) {
        this.semaphore = new Semaphore(exportProperties.getLimit().getMaxConcurrentExports(), true);
    }

    @Override
    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    @Override
    public void release() {
        semaphore.release();
    }
}
