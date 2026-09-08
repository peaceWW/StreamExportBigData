package cn.sdpjw.export.handler;

import cn.sdpjw.export.stub.dto.ExportRequest;
import cn.sdpjw.export.stub.dto.ExportUserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * MyBatis XML 统一查询参数包装
 */
@Data
@AllArgsConstructor
public class ExportQueryParam<T extends Serializable> {

    private ExportUserInfo user;
    private T query;
    private ExportCheckpoint checkpoint;
}
