package cn.sdpjw.export.core.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式导出统一返回对象
 * 
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponse<T> {
    
    /**
     * 响应码
     * 0: 成功
     * 1: 失败
     * 2: 未查询到数据
     */
    private Integer code;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 成功响应
     */
    public static <T> ExportResponse<T> success(T data) {
        return new ExportResponse<>(0, data, "操作成功");
    }
    
    /**
     * 成功响应（带消息）
     */
    public static <T> ExportResponse<T> success(T data, String message) {
        return new ExportResponse<>(0, data, message);
    }
    
    /**
     * 失败响应
     */
    public static <T> ExportResponse<T> fail(String message) {
        return new ExportResponse<>(1, null, message);
    }
    
    /**
     * 未查询到数据响应
     */
    public static <T> ExportResponse<T> noData(String message) {
        return new ExportResponse<>(2, null, message);
    }
    
    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 0;
    }
    
    /**
     * 判断是否未查询到数据
     */
    public boolean isNoData() {
        return code != null && code == 2;
    }
    
    /**
     * 判断是否失败
     */
    public boolean isFail() {
        return code != null && code == 1;
    }
}

