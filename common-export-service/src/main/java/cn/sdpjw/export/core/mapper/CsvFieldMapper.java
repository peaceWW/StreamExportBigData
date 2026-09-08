package cn.sdpjw.export.core.mapper;

/**
 * CSV字段映射器接口
 * 用于将实体对象映射为CSV行数据
 * 
 * @param <T> 实体类型
 */
public interface CsvFieldMapper<T> {
    /**
     * 获取CSV表头
     * 
     * @return 表头数组
     */
    String[] getHeaders();
    
    /**
     * 将实体转换为CSV行数据
     * 
     * @param entity 实体对象
     * @return CSV行数据数组
     */
    Object[] mapToRow(T entity);
    
    /**
     * 获取字段数量（用于验证）
     * 
     * @return 字段数量
     */
    default int getFieldCount() {
        String[] headers = getHeaders();
        return headers != null ? headers.length : 0;
    }
}

