package web.sy.base.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import web.sy.base.config.event.BedSystemInitializedEvent;
import web.sy.base.event.ConfigChangedEvent;
import web.sy.base.pojo.entity.SystemConfig;
import web.sy.base.service.SystemConfigService;
import web.sy.base.utils.SQLiteUtils;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)  // 现在 GlobalConfig 需要最先初始化
public class GlobalConfig {
    
    @Getter
    private static SystemConfig config;
    @Getter
    private static Boolean initialized;
    @Getter
    private static String dbUrl;
    @Getter
    private static String dbUsername;
    @Getter
    private static String dbPassword;
    @Getter
    public static final String[] swaggerUrls = new String[]{"/swagger-ui/**", "/v3/api-docs/swagger-config", "/v3/api-docs"};

    private final SystemConfigService systemConfigService;
    private final ApplicationContext applicationContext;

    public GlobalConfig(SystemConfigService systemConfigService, ApplicationContext applicationContext) {
        this.systemConfigService = systemConfigService;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        SQLiteUtils.initSqliteTable();  // 初始化数据库表
        refreshConfig();
        setupMySQLProperties();
        if (initialized) {
            applicationContext.publishEvent(new BedSystemInitializedEvent(this));
        }
    }

    @EventListener
    public void handleConfigChangedEvent(ConfigChangedEvent event) {
        refreshConfig();
        if (event.getKey().startsWith("db.")) {
            setupMySQLProperties();
            if (initialized) {
                applicationContext.publishEvent(new BedSystemInitializedEvent(this));
            }
        }
    }

    private synchronized void refreshConfig() {
        config = SQLiteUtils.getSystemConfig();
        initialized = systemConfigService.isInitialized();
        dbUrl = systemConfigService.getConfigValue("db.url");
        dbUsername = systemConfigService.getConfigValue("db.username");
        dbPassword = systemConfigService.getConfigValue("db.password");
    }

    private void setupMySQLProperties() {
        if (dbUrl != null && dbUsername != null && dbPassword != null) {
            System.setProperty("spring.datasource.url", dbUrl);
            System.setProperty("spring.datasource.username", dbUsername);
            System.setProperty("spring.datasource.password", dbPassword);
            System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        }
    }
} 