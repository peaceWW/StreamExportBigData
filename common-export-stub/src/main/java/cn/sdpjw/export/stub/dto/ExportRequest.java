package cn.sdpjw.export.stub.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一导出请求
 */
@Data
public class ExportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private ExportUserInfo userInfo;
    private String menuCode;
    private LocalDateTime clientRequestTime;

    /**
     * HTTP 入口原始 JSON，用于保留业务查询字段；入库后写入 queryParams
     */
    private transient String rawRequestJson;


}