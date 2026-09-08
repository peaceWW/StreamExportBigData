package cn.sdpjw.export.config;


import cn.sdpjw.export.entity.ExportTask;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * TaskStatus 枚举类型处理器
 * 将 TaskStatus 枚举与数据库中的 int 类型进行转换
 * 使用枚举的 code 值进行存储和读取
 */
@MappedTypes(ExportTask.TaskStatus.class)
@MappedJdbcTypes(JdbcType.INTEGER)
public class TaskStatusTypeHandler extends BaseTypeHandler<ExportTask.TaskStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ExportTask.TaskStatus parameter, JdbcType jdbcType) throws SQLException {
        // 将枚举的 code 值写入数据库
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public ExportTask.TaskStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从数据库读取 int 值，转换为枚举
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : ExportTask.TaskStatus.fromCode(code);
    }

    @Override
    public ExportTask.TaskStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 从数据库读取 int 值，转换为枚举
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : ExportTask.TaskStatus.fromCode(code);
    }

    @Override
    public ExportTask.TaskStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从存储过程读取 int 值，转换为枚举
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : ExportTask.TaskStatus.fromCode(code);
    }
}
