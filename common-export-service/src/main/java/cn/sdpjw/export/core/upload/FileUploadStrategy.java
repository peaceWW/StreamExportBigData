package cn.sdpjw.export.core.upload;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件上传策略接口
 * 支持多种文件上传方式（本地、OSS、FTP等）
 */
public interface FileUploadStrategy {
    /**
     * 上传文件
     * 
     * @param filePath 本地文件路径
     * @param remotePath 远程路径
     * @return 远程文件URL或路径
     * @throws IOException IO异常
     */
    String upload(String filePath, String remotePath) throws IOException;
    
    /**
     * 下载文件（从远程URL或路径下载）
     * 
     * @param remoteUrl 远程文件URL或路径
     * @return 文件输入流
     * @throws IOException IO异常
     */
    default InputStream download(String remoteUrl) throws IOException {
        throw new UnsupportedOperationException("该策略不支持下载: " + getType());
    }
    
    /**
     * 判断是否为该策略支持的URL格式
     * 
     * @param url URL字符串
     * @return 是否支持
     */
    default boolean supportsUrl(String url) {
        return false;
    }
    
    /**
     * 上传策略类型
     * 
     * @return 策略类型（如：local、oss、ftp）
     */
    String getType();
    
    /**
     * 是否支持该策略
     * 
     * @param type 策略类型
     * @return 是否支持
     */
    default boolean supports(String type) {
        return getType().equals(type);
    }
}

