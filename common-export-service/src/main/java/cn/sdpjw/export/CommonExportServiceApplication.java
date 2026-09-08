package cn.sdpjw.export;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 公共导出服务启动类
 */
@EnableAsync
@EnableScheduling
@MapperScan("cn.sdpjw.export.dao")
@SpringBootApplication(scanBasePackages = {"cn.sdpjw.export"})
public class CommonExportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonExportServiceApplication.class, args);
    }
}
