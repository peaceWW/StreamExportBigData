package cn.sdpjw.export.core.context;

/**
 * 导出上下文
 * 
 * <p>用于标识当前线程是否处于导出执行状态，以及导出限制值。
 * 通过 ThreadLocal 实现线程隔离，确保导出拦截器只影响导出查询。</p>
 * 
 * <p>使用方式：</p>
 * <pre>
 * // 在导出入口设置
 * ExportContext.setExportLimit(10000);
 * try {
 *     // 执行导出查询
 * } finally {
 *     ExportContext.clear();
 * }
 * 
 * // 在拦截器中判断
 * if (ExportContext.isInExportContext()) {
 *     Integer limit = ExportContext.getLimit();
 *     // 添加 LIMIT
 * }
 * </pre>
 * 
 * @author batch-export
 */
public class ExportContext {
    
    /**
     * 导出限制值（ThreadLocal）
     * 当设置此值时，表示当前线程处于导出执行状态
     */
    private static final ThreadLocal<Integer> EXPORT_LIMIT = new ThreadLocal<>();
    
    /**
     * 设置导出限制值，同时标识当前线程处于导出状态
     * 
     * @param limit 导出限制值（最大导出条数）
     */
    public static void setExportLimit(Integer limit) {
        if (limit != null && limit > 0) {
            EXPORT_LIMIT.set(limit);
        } else {
            clear();
        }
    }
    
    /**
     * 获取当前线程的导出限制值
     * 
     * @return 导出限制值，如果不在导出上下文中则返回 null
     */
    public static Integer getLimit() {
        return EXPORT_LIMIT.get();
    }
    
    /**
     * 判断当前线程是否处于导出执行状态
     * 
     * @return true 表示当前线程正在执行导出，false 表示非导出线程
     */
    public static boolean isInExportContext() {
        return EXPORT_LIMIT.get() != null;
    }
    
    /**
     * 清除当前线程的导出上下文
     * 应在导出执行完成后（finally 块中）调用，避免内存泄漏
     */
    public static void clear() {
        EXPORT_LIMIT.remove();
    }
}
