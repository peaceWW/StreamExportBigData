package cn.sdpjw.export.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 阿里云OSS配置属性
 * 支持 Nacos 配置动态刷新
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "export.oss")
public class OssConfigProperties {
    
    /**
     * AccessKey ID
     */
    private String accessKeyId = "";
    
    /**
     * AccessKey Secret
     */
    private String accessKeySecret = "";
    
    /**
     * OSS Endpoint
     */
    private String endpoint = "";
    
    /**
     * OSS Bucket名称
     */
    private String bucketName = "";
    
    /**
     * 签名URL过期时间（秒）
     */
    private Long expired = 3600L;


    /**
     * OSS 公网访问地址（用于生成返回给外部的 URL）
     * 格式：https://{bucket-name}.{region}.aliyuncs.com
     * 示例：https://erp-qa.oss-cn-beijing.aliyuncs.com
     * 可选：如果不配置，将基于 endpoint 自动构建
     */
    private String host = "";

    /**
     * 智能媒体服务（IMM）端点地址（可选）
     * 格式：imm.{region}.aliyuncs.com
     * 示例：imm.cn-beijing.aliyuncs.com
     * 用途：文档预览、图片处理等智能媒体服务
     */
    private String immEndpoint = "";

    /**
     * OSS 内网访问地址（用于容器/ECS 内访问，节省流量且速度更快）
     * 格式：oss-{region}-internal.aliyuncs.com 或 https://oss-{region}-internal.aliyuncs.com
     * 示例：oss-cn-beijing-internal.aliyuncs.com
     * 可选：仅当 useInternal=true 且与本服务同地域部署在阿里云内网时启用
     * 注意：本地开发或公网环境无法访问内网地址，请保持 useInternal=false
     */
    private String internal = "";

    /**
     * 是否使用 internal 内网地址创建 OSS 客户端
     * 默认 false；仅在阿里云 ECS/容器与 OSS 同地域时设为 true
     */
    private Boolean useInternal = false;
}
