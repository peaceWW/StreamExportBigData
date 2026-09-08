package cn.sdpjw.export.stub.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 导出请求人信息
 */
@Data
public class ExportUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer employeeId;
    private String employeeName;
    private String deptCode;
    private String subscriberApp;
    private Map<String, String> ext;
}
