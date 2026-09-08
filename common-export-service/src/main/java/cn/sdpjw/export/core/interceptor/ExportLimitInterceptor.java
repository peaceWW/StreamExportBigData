package cn.sdpjw.export.core.interceptor;

import cn.sdpjw.export.core.context.ExportContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 导出限制拦截器
 * 动态在SQL中添加LIMIT参数，实现导出条数上限控制
 * 
 * <p>只对导出上下文中的查询生效，不会影响其他表的查询（如 export_tasks）。</p>
 * <p>通过 {@link ExportContext} 来判断当前线程是否处于导出执行状态。</p>
 */
@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {
            MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class
    })
})
public class ExportLimitInterceptor implements Interceptor {
    
    /**
     * SQL中是否已包含LIMIT的正则表达式
     */
    private static final Pattern LIMIT_PATTERN = Pattern.compile(
            "\\bLIMIT\\s+\\d+(\\s*,\\s*\\d+)?", 
            Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 排除的表名（这些表的查询不会被添加LIMIT）
     * 用于避免影响导出任务表（export_tasks）等系统表的查询
     */
    private static final Pattern EXCLUDE_TABLE_PATTERN = Pattern.compile(
            "\\bFROM\\s+(?:`?export_tasks`?|`?export_task`?)", 
            Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 检查是否在导出上下文中，如果不在，直接放行（不影响其他查询）
        if (!ExportContext.isInExportContext()) {
            return invocation.proceed();
        }
        
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        
        // 只处理SELECT查询
        if (mappedStatement.getSqlCommandType() != SqlCommandType.SELECT) {
            return invocation.proceed();
        }
        
        // 获取原始SQL
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originalSql = boundSql.getSql().trim();
        
        // 检查是否是排除的表（如 export_tasks），如果是则跳过拦截
        if (EXCLUDE_TABLE_PATTERN.matcher(originalSql).find()) {
            log.debug("跳过导出任务表的查询，不添加LIMIT: {}", originalSql);
            return invocation.proceed();
        }
        
        // 检查SQL中是否已包含LIMIT
        if (LIMIT_PATTERN.matcher(originalSql).find()) {
            log.debug("SQL中已包含LIMIT，跳过拦截: {}", originalSql);
            return invocation.proceed();
        }
        
        // 获取导出限制值（从 ExportContext 获取）
        Integer limit = ExportContext.getLimit();
        if (limit == null || limit <= 0) {
            return invocation.proceed();
        }
        
        // 在SQL末尾添加LIMIT
        String newSql = originalSql + " LIMIT " + limit;
        
        log.debug("拦截SQL并添加LIMIT: 原始SQL={}, 新SQL={}", originalSql, newSql);
        
        // 创建新的BoundSql
        BoundSql newBoundSql = new BoundSql(
                mappedStatement.getConfiguration(),
                newSql,
                boundSql.getParameterMappings(),
                parameter
        );
        
        // 复制原始参数
        for (org.apache.ibatis.mapping.ParameterMapping mapping : boundSql.getParameterMappings()) {
            String property = mapping.getProperty();
            Object value = boundSql.getAdditionalParameter(property);
            if (value != null) {
                newBoundSql.setAdditionalParameter(property, value);
            }
        }
        
        // 创建新的MappedStatement
        MappedStatement newMappedStatement = copyFromMappedStatement(mappedStatement, newBoundSql);
        
        // 替换参数
        Object[] args = invocation.getArgs();
        args[0] = newMappedStatement;
        
        // 执行原方法
        return invocation.proceed();
    }
    
    /**
     * 复制MappedStatement
     */
    private MappedStatement copyFromMappedStatement(MappedStatement ms, BoundSql newBoundSql) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(),
                ms.getId(),
                parameterObject -> newBoundSql,
                ms.getSqlCommandType()
        );
        
        builder.resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(ms.getKeyGenerator())
                .keyProperty(ms.getKeyProperties() != null ? String.join(",", ms.getKeyProperties()) : null)
                .keyColumn(ms.getKeyColumns() != null ? String.join(",", ms.getKeyColumns()) : null)
                .databaseId(ms.getDatabaseId())
                .lang(ms.getLang())
                .resultOrdered(ms.isResultOrdered())
                .resultSets(ms.getResultSets() != null ? String.join(",", ms.getResultSets()) : null)
                .resultMaps(ms.getResultMaps())
                .resultSetType(ms.getResultSetType())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .cache(ms.getCache());
        
        return builder.build();
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
        // 从properties中读取配置（如果需要）
    }
}

