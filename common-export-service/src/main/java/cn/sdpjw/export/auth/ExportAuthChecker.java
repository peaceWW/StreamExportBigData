package cn.sdpjw.export.auth;

import cn.sdpjw.export.stub.dto.ExportRequest;

/**
 * 导出鉴权（预留）
 */
public interface ExportAuthChecker {

    void checkCreatePermission(Integer employeeId);

    void checkDownloadPermission(Integer employeeId, String taskId);
}
