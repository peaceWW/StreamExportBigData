package cn.sdpjw.export.stub.dto.query;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * demo页查询请求DTO
 *
 * @author 吴
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DemoRequestDTO extends ExportRequest {
    private Integer traderCorpId;
    private String startTime;
    private String endTime;
}
