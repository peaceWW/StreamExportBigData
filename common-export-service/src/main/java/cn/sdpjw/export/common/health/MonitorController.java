package cn.sdpjw.export.common.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorController {

    @GetMapping("/monitor/health")
    public String health() {
        return "success";
    }
}
