package cn.sdpjw.export.core.enricher;

import java.util.List;

/**
 * 数据增强函数式接口
 * 用于批量补充第三方业务模块数据
 * 
 * @param <T> 输入实体类型
 * @param <R> 输出实体类型（通常是增强后的T类型）
 */
@FunctionalInterface
public interface DataEnricher<T, R> {
    /**
     * 批量增强数据
     * 
     * @param batch 原始批次数据
     * @return 增强后的批次数据
     */
    List<R> enrich(List<T> batch);
}

