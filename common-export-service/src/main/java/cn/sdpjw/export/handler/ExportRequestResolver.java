package cn.sdpjw.export.handler;

import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.export.stub.dto.ExportRequest;
import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

/**
 * 按 Handler 声明的 queryType 将请求反序列化为具体 DTO
 */
public final class ExportRequestResolver {

    private ExportRequestResolver() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends ExportRequest> T resolve(ExportHandler<T> handler, String queryParamsJson) {
        if (!StringUtils.hasText(queryParamsJson)) {
            throw new BusinessException("queryParams 不能为空");
        }
        return JSON.parseObject(queryParamsJson, handler.queryType());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ExportRequest> T resolve(ExportHandler<T> handler, ExportRequest request) {
        Class<T> queryType = handler.queryType();
        if (queryType.isInstance(request)) {
            return (T) request;
        }
        return JSON.parseObject(JSON.toJSONString(request), queryType);
    }
}
