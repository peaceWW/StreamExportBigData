package cn.sdpjw.export.core.provider;

/**
 * 数据统计提供者接口
 * 用于获取数据总数
 */
@FunctionalInterface
public interface CountProvider {
    /**
     * 获取数据总数
     * 
     * @return 数据总数
     */
    long count();
}

