package cn.sdpjw.export.config;



import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 同步 export.stream 配置到 batch-export-core
 */
@Configuration
@EnableConfigurationProperties(ExportConfigProperties.class)
@RequiredArgsConstructor
public class BatchExportCoreConfig {

    private final ExportProperties exportProperties;
    private final ExportConfigProperties exportConfigProperties;

    @PostConstruct
    public void syncCoreProperties() {
        ExportProperties.Stream stream = exportProperties.getStream();
        exportConfigProperties.setBatchSize(stream.getBatchSize());
        exportConfigProperties.setMaxExportRows(stream.getMaxExportRows());
        exportConfigProperties.setCsvMaxRows(stream.getCsvMaxRows());
        exportConfigProperties.setZipEnabled(stream.isZipEnabled());
        exportConfigProperties.setCharset(stream.getCharset());
        exportConfigProperties.setCleanupLocal(stream.isCleanupLocal());
        exportConfigProperties.setTempDir(stream.getTempDir());
        exportConfigProperties.setExportDir(stream.getExportDir());
        exportConfigProperties.setUploadType(stream.getUploadType());
    }
}
