package cn.sdpjw.export.stub.request;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @Description: 对账查询请求体
 * @Author: songbaicheng
 * @Create: 2024/10/15 18:25
 **/
@Setter
@Getter
@ToString
public class ElectronicBillFlowListExportRequest extends ExportRequest implements Serializable  {

    private static final long serialVersionUID = 6722037915319581734L;

    /**
     * 交易渠道
     */
    private Integer channel;

    /**
     * 企业名称
     */
    private String corpName;

    /**
     * 开始时间
     */
    private LocalDate startDate;

    /**
     * 结束时间
     */
    private LocalDate endDate;

    /**
     * 客户经理id
     */
    private List<Integer> customerManagerEmployeeIds;

    /**
     * 客户经理所属部门id
     */
    private List<Integer> customerManagerDeptIds;

    /**
     * 平台类型 6-承接 7-承载
     */
    private Integer registerType;
}