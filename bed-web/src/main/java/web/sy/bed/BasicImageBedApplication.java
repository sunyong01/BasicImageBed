package web.sy.bed;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import web.sy.bed.base.config.GlobalConfig;
import web.sy.bed.base.config.LoggingConfig;
import web.sy.bed.base.pojo.entity.SystemConfig;


@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
}, scanBasePackages = {"web.sy.bed", "web.sy.storage"}
)
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
