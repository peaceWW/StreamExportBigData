package cn.sdpjw.export.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 导出模块配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "export")
public class ExportProperties {

    private String metaDatasource = "meta";

    private Stream stream = new Stream();

    private Limit limit = new Limit();

    private ThreadPool threadPool = new ThreadPool();

    private Map<String, String> datasourceRouting = new HashMap<>();

    @Data
    public static class Stream {
        private Integer maxExportRows;
        private int batchSize = 5000;
        private int csvMaxRows = 100000;
        private boolean zipEnabled = true;
        private String charset = "UTF-8";
        private boolean autoUpload = true;
        private boolean cleanupLocal = true;
        private String tempDir = "./export/temp";
        private String exportDir = "./export";
        private String uploadType = "oss";
    }

    @Data
    public static class Limit {
        private int maxConcurrentExports = 3;
        private int maxRetry = 3;
        private int runningTimeoutMinutes = 10;
    }

    @Data
    public static class ThreadPool {
        private int coreSize = 2;
        private int maxSize = 4;
        private int queueCapacity = 50;
        private int keepAliveSeconds = 60;
    }
}
