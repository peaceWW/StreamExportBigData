package cn.sdpjw.export.core.provider;

import java.util.stream.Stream;

/**
 * 流式数据提供者接口
 * 用于在事务中执行流式查询
 * 
 * @param <T> 实体类型
 */
@FunctionalInterface
public interface StreamDataProvider<T> {
    /**
     * 在事务中执行流式查询
     * 
     * @return Stream<T> 数据流
     */
    Stream<T> provide();
}

