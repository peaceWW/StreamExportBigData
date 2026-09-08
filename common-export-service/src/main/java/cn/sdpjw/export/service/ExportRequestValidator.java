package cn.sdpjw.export.service;

import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.export.stub.dto.ExportRequest;
import cn.sdpjw.export.stub.dto.ExportUserInfo;
import cn.sdpjw.export.stub.enums.MenuEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 导出请求校验
 */
@Component
public class ExportRequestValidator {

    public void validate(ExportRequest request) {
        if (request == null) {
            throw new BusinessException("请求不能为空");
        }
        if (!StringUtils.hasText(request.getMenuCode())) {
            throw new BusinessException("menuCode 不能为空");
        }
        MenuEnum menuEnum = MenuEnum.of(request.getMenuCode());
        if (menuEnum == null) {
            throw new BusinessException("未知的 menuCode: " + request.getMenuCode());
        }
        if (!menuEnum.isExportable()) {
            throw new BusinessException("menuCode 不支持导出: " + request.getMenuCode());
        }
        ExportUserInfo userInfo = request.getUserInfo();
        if (userInfo == null || userInfo.getEmployeeId() == null) {
            throw new BusinessException("操作人 employeeId 不能为空");
        }

    }
}
