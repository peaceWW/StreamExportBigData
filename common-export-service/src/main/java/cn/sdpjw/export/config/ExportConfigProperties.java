package cn.sdpjw.export.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 导出配置属性
 * 支持 Nacos 配置动态刷新
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "export.stream")
public class ExportConfigProperties {
    
    /**
     * 批次大小
     */
    private int batchSize = 5000;
    
    /**
     * 导出条数上限（可选，如果配置且大于0，则在SQL中自动添加LIMIT）
     */
    private Integer maxExportRows = 500000;
    
    /**
     * 单个CSV最大行数
     * - 当zip-enabled=true时：单个CSV文件的最大行数，超过则创建新文件
     * - 当zip-enabled=false时：单个sheet页的最大行数，超过则在同一文件中创建新sheet（重新写入表头）
     */
    private int csvMaxRows = 100000;
    
    /**
     * 是否启用ZIP压缩
     * true: 多个CSV文件打包成ZIP（超过csv-max-rows创建新文件）
     * false: 只生成单个CSV文件（超过csv-max-rows在同一文件中创建新sheet，不打包）
     */
    private boolean zipEnabled = true;
    
    /**
     * 文件编码
     */
    private String charset = "UTF-8";
    
    /**
     * 上传后是否清理本地文件
     */
    private boolean cleanupLocal = true;
    
    /**
     * 临时目录
     */
    private String tempDir = "./export/temp";
    
    /**
     * 导出目录
     */
    private String exportDir = "./export";
    
    /**
     * 上传类型：local/oss/ftp
     */
    private String uploadType = "oss";
    
    /**
     * 数据增强超时时间（秒）
     * 如果第三方服务响应时间超过此值，将使用原始数据继续导出
     * 默认值：5秒，设置为0或负数表示不限制超时
     */
    private int enrichTimeoutSeconds = 5;
}

