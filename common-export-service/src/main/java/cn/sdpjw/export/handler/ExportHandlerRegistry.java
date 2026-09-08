package cn.sdpjw.export.handler;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler 注册中心
 */
@Slf4j
@Component
public class ExportHandlerRegistry {

    private final Map<String, ExportHandler<?>> handlerMap = new HashMap<>();

    public ExportHandlerRegistry(List<ExportHandler<?>> handlers) {
        for (ExportHandler<?> handler : handlers) {
            if (handlerMap.containsKey(handler.menuCode())) {
                throw new IllegalStateException("重复的导出 Handler menuCode: " + handler.menuCode());
            }
            handlerMap.put(handler.menuCode(), handler);
            log.info("注册导出 Handler: menuCode={}, type={}", handler.menuCode(), handler.getClass().getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ExportRequest> ExportHandler<T> getHandler(String menuCode) {
        ExportHandler<?> handler = handlerMap.get(menuCode);
        if (handler == null) {
            throw new IllegalArgumentException("未注册导出 Handler: " + menuCode);
        }
        return (ExportHandler<T>) handler;
    }


}
