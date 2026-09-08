package cn.sdpjw.export.stub.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class ExportPageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private long total;
    private int pageNum;
    private int pageSize;
    private List<T> list;

    public static <T> ExportPageResult<T> empty(int pageNum, int pageSize) {
        ExportPageResult<T> result = new ExportPageResult<>();
        result.setTotal(0);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(Collections.emptyList());
        return result;
    }
}
