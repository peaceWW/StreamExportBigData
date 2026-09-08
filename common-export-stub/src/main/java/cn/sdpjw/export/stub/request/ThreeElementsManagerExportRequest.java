package cn.sdpjw.export.stub.request;

import cn.sdpjw.export.stub.dto.ExportRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: liuyuebai
 * @date: 2026/7/20 16:29
 * @description:
 */
@Setter
@Getter
@ToString
public class ThreeElementsManagerExportRequest extends ExportRequest implements Serializable {
    private static final long serialVersionUID = 1969046460505641418L;

    /**
     * 企业名称
     */
    private String corpName;

    /**
     * 校验项 0-法人三要素验证，1-经办人三要素验证
     */
    private Integer checkItem;

    /**
     * 当前状态 0-开启，1-关闭
     */
    private Integer openStatus;

}
