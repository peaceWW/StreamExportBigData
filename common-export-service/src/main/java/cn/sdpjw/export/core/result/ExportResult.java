package cn.sdpjw.export.core.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导出结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportResult {
    /**
     * 本地文件路径
     */
    private String localFilePath;
    
    /**
     * 远程文件URL或路径
     */
    private String remoteUrl;
    
    /**
     * 上传类型
     */
    private String uploadType;
    
    /**
     * 文件数量
     */
    private int fileCount;
    
    /**
     * 总行数
     */
    private long totalRows;
}

