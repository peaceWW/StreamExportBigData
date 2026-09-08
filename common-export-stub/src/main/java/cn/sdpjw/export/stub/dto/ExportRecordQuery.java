package cn.sdpjw.export.stub.dto;

import cn.sdpjw.common.base.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 导出记录列表查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportRecordQuery extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer employeeId;
    private String menuCode;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String subscriberApp;
    private int pageNum = 1;
    private int pageSize = 20;
}
