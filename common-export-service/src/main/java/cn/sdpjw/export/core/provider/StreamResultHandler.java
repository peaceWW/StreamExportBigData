package cn.sdpjw.export.core.provider;

/**
 * 流式结果处理器接口
 * 用于在事务中直接处理查询结果，实现真正的流式处理
 * 
 * @param <T> 实体类型
 */
@FunctionalInterface
public interface StreamResultHandler<T> {
    /**
     * 处理单条查询结果
     * 
     * @param result 查询结果对象
     * @return true 继续处理，false 停止处理
     */
    boolean handle(T result);
}

