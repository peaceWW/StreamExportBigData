package cn.sdpjw.export.entity;


import cn.sdpjw.export.config.LocalDateTimeTypeHandler;
import cn.sdpjw.export.config.TaskStatusTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 导出元数据记录（与 core ExportTask 共用 export_data_task 表）
 * <p>
 * core 写入：status、progress、processedRows、totalRows、exportUrl、exportName(filename)、errorMessage、operatorEmployeeId、createdAt、updatedAt
 * <br>
 * 业务层写入：menuCode、queryParams、subscriberApp、dataSourceKey、operatorName、checkpoint*、retryCount、receivedAt、exportTime、fileFormat、version
 */
@Data
@TableName(value = "export_data_task", autoResultMap = true)
public class ExportTask {

    /**
     * 主键ID
     * 使用手动输入策略(IdType.INPUT),由业务系统自行生成
     */
    //@TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 关联的导出任务ID
     * 用于关联到具体的导出任务(ExportTask)
     */
    private String taskId;

    /**
     * 菜单编码
     * 标识数据来源的业务模块或菜单,用于权限控制和数据溯源
     */
    private String menuCode;

    /**
     * 操作人员ID
     * 发起导出操作的员工ID
     */
    private Integer operatorEmployeeId;

    /**
     * 操作人员姓名
     * 发起导出操作的员工姓名
     */
    private String operatorName;

    /**
     * 订阅者应用标识
     * 标识请求导出的应用系统,用于多应用场景下的区分
     */
    private String subscriberApp;

    /**
     * 数据源标识
     * 标识数据来源的数据源key,支持多数据源场景
     */
    private String dataSourceKey;

    /**
     * 查询参数
     * JSON格式的查询条件,记录导出时使用的筛选条件
     */
    private String queryParams;

    /**
     * 导出文件URL
     * 导出完成后文件的访问地址或下载链接
     */
    private String exportUrl;

    /**
     * 导出文件名称
     * 生成的导出文件的名称
     */
    private String exportName;

    /**
     * 文件格式
     * 导出文件的格式,如: Excel, CSV, PDF等
     */
    private String fileFormat;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 已处理行数
     * 当前已处理的数据行数,用于进度计算
     */
    private Long processedRows;

    /**
     * 总数据行数
     * 需要导出的数据总行数
     */
    private Long totalRows;

    /**
     * 导出进度百分比
     * 取值范围0-100,表示导出任务的完成进度
     */
    private Integer progress;

    /**
     * 导出状态
     * 常见状态值: 0-待处理, 1-处理中, 2-成功, 3-失败, 4-已取消
     */
    @TableField(value = "status", typeHandler = TaskStatusTypeHandler.class)
    private TaskStatus status;

    /**
     * 错误信息
     * 导出失败时的错误描述信息
     */
    private String errorMessage;

    /**
     * 检查点最后处理的ID
     * 用于断点续传,记录最后一次成功处理的数据ID
     */
    private String checkpointLastId;

    /**
     * 检查点分片编号
     * 用于分片导出场景,记录当前处理的分片编号
     */
    private Integer checkpointPartNo;

    /**
     * 临时文件路径
     * 导出过程中生成的临时文件存储路径
     */
    private String tempFilePath;

    /**
     * 重试次数
     * 导出失败后的重试次数记录
     */
    private Integer retryCount;

    /**
     * 接收时间
     * 系统接收到导出请求的时间
     */
    private LocalDateTime receivedAt;

    /**
     * 导出完成时间
     * 导出任务执行完成的时间
     */
    private LocalDateTime exportTime;

    /**
     * 更新时间
     * 记录最后更新时间,用于乐观锁或审计
     */
    @TableField(value = "updated_at", typeHandler = LocalDateTimeTypeHandler.class)
    private LocalDateTime updatedAt;

    /**
     * 创建时间
     * 记录创建时间
     */
    @TableField(value = "created_at", typeHandler = LocalDateTimeTypeHandler.class)
    private LocalDateTime createdAt;

    /**
     * 乐观锁版本号
     * 用于并发控制,防止并发更新导致的数据不一致
     */
    @Version
    private Integer version;


    /**
     * 插入前设置创建时间和更新时间
     */
    public void preInsert() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新前设置更新时间
     */
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Getter
    public enum TaskStatus {
        // 1. 调用构造函数，为每个枚举值指定状态码
        PENDING(0, "待执行"),
        RUNNING(1, "执行中"),
        SUCCESS(2, "成功"),
        FAILED(3, "失败"),
        CANCELLED(4, "已取消"),
        RECOVERABLE(5, "可恢复");

        // 2. 定义私有字段，用于存储状态码
        private final int code;
        private final String desc;

        // 3. 定义构造函数
        TaskStatus(int code,String desc) {
            this.code = code;
            this.desc = desc;
        }

        /**
         * (可选) 根据状态码查找对应的枚举值
         * @param code 要查找的状态码
         * @return 对应的 TaskStatus，如果找不到则返回 null
         */
        public static ExportTask.TaskStatus fromCode(int code) {
            for (ExportTask.TaskStatus status : ExportTask.TaskStatus.values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return null;
        }     // 失败
    }
}