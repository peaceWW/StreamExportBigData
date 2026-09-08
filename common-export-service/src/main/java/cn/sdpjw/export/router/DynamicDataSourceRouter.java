package cn.sdpjw.export.router;

import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.export.config.ExportProperties;
import cn.sdpjw.export.stub.enums.MenuEnum;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 基于 menuType 配置的动态数据源路由
 */
@Component
@RequiredArgsConstructor
public class DynamicDataSourceRouter implements DataSourceRouter {

    private final ExportProperties exportProperties;

    @Override
    public String resolve(String menuCode) {
        MenuEnum menuEnum = MenuEnum.of(menuCode);
        if (menuEnum == null) {
            throw new IllegalArgumentException("未知的 menuCode: " + menuCode);
        }
        String dataSourceKey = exportProperties.getDatasourceRouting().get(menuCode);
        if (!StringUtils.hasText(dataSourceKey)) {
            throw new BusinessException("menuCode 未配置数据源: " + menuCode);
        }
        return dataSourceKey;
    }

    @Override
    public <T> T execute(String dataSourceKey, java.util.function.Supplier<T> action) {
        DynamicDataSourceContextHolder.push(dataSourceKey);
        try {
            return action.get();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    @Override
    public <T> T executeOnMeta(java.util.function.Supplier<T> action) {
        return execute(exportProperties.getMetaDatasource(), action);
    }
}
