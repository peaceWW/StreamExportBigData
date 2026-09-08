package cn.sdpjw.export.router;

import cn.sdpjw.export.config.ExportProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 监听配置变更，刷新 menuType 数据源路由
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExportDataSourceRefreshListener {

    private final ExportProperties exportProperties;

    @PostConstruct
    public void init() {
        log.info("Export datasource routing loaded: {}", exportProperties.getDatasourceRouting());
    }

    @EventListener
    public void onEnvironmentChange(EnvironmentChangeEvent event) {
        boolean changed = event.getKeys().stream()
                .anyMatch(key -> key.startsWith("export.datasource-routing")
                        || key.startsWith("spring.datasource.dynamic.datasource."));
        if (changed) {
            log.info("Export datasource config refreshed, routing={}", exportProperties.getDatasourceRouting());
        }
    }
}
