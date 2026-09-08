package cn.sdpjw.export.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 导出模块自动配置类
 */
@Configuration
@ComponentScan(basePackages = "cn.sdpjw.export.core")
@EnableConfigurationProperties({ExportConfigProperties.class, OssConfigProperties.class})
public class ExportAutoConfiguration {
    // 自动扫描并注册所有核心组件
}

