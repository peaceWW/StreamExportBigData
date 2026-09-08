package cn.sdpjw.export.core.upload;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件上传策略工厂
 */
@Slf4j
@Component
public class FileUploadStrategyFactory {
    
    private final List<FileUploadStrategy> strategies;
    
    @Autowired
    public FileUploadStrategyFactory(List<FileUploadStrategy> strategies) {
        this.strategies = strategies;
        log.info("初始化文件上传策略工厂，共{}个策略", strategies.size());
    }
    
    /**
     * 获取上传策略
     * 
     * @param type 策略类型
     * @return 上传策略
     * @throws IllegalArgumentException 如果不支持该类型
     */
    public FileUploadStrategy getStrategy(String type) {
        log.info("FileUploadStrategyFactory getStrategy parameters:{}",type);
        return strategies.stream()
            .filter(s -> s.supports(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("不支持的上传类型: " + type));
    }
}

