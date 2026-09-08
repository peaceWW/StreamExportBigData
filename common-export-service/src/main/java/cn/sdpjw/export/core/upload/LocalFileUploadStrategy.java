package cn.sdpjw.export.core.upload;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地文件上传策略（默认）
 */
@Slf4j
@Service
public class LocalFileUploadStrategy implements FileUploadStrategy {
    
    @Override
    public String getType() {
        return "local";
    }
    
    @Override
    public String upload(String filePath, String remotePath) throws IOException {
        // 本地文件直接返回路径
        log.debug("本地文件上传策略，文件路径: {}", filePath);
        return filePath;
    }
    
    @Override
    public InputStream download(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }
        return new FileInputStream(file);
    }
    
    @Override
    public boolean supportsUrl(String url) {
        // 本地文件路径判断：不是http/https开头的URL
        if (url == null || url.isEmpty()) {
            return false;
        }
        return !url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("ftp://");
    }
}

