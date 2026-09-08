package cn.sdpjw.export.auth;

import cn.sdpjw.export.stub.dto.ExportRequest;
import org.springframework.stereotype.Component;

@Component
public class NoOpExportAuthChecker implements ExportAuthChecker {

    @Override
    public void checkCreatePermission(Integer employeeId) {
        // 预留鉴权实现
    }

    @Override
    public void checkDownloadPermission(Integer employeeId, String taskId) {
        // 预留鉴权实现
    }
}
