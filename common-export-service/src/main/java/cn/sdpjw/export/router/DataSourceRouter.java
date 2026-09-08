package cn.sdpjw.export.router;

/**
 * menuType 数据源路由
 */
public interface DataSourceRouter {

    String resolve(String menuCode);

    <T> T execute(String dataSourceKey, java.util.function.Supplier<T> action);

    /**
     * 在元数据库上下文中执行（export_data_task 等元数据表专用）
     */
    <T> T executeOnMeta(java.util.function.Supplier<T> action);

    default void runOnMeta(Runnable action) {
        executeOnMeta(() -> {
            action.run();
            return null;
        });
    }
}
