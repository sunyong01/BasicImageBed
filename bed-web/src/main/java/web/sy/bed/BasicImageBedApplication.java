package web.sy.bed;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import web.sy.base.config.GlobalConfig;
import web.sy.base.config.LoggingConfig;
import web.sy.base.pojo.entity.SystemConfig;


@SpringBootApplication(scanBasePackages = {"web.sy.bed", "web.sy.storage", "web.sy.base"})
public class BasicImageBedApplication {
    @Autowired
    private LoggingConfig loggingConfig;

    public static void main(String[] args) {
        SpringApplication.run(BasicImageBedApplication.class, args);
    }

    @PostConstruct
    public void init() {
        SystemConfig config = GlobalConfig.getConfig();
        if (config != null) {
            Boolean debug = config.getDebug();
            if (debug != null) {
                loggingConfig.updateLogLevel(debug);
            }
        }
    }
}
