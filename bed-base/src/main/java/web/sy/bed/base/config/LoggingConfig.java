package web.sy.bed.base.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import web.sy.bed.base.event.ConfigChangeEvent;

@Slf4j
@Configuration
public class LoggingConfig {
    
    private static final String BASE_PACKAGE = "web.sy.bed";
    
    @EventListener
    public void onConfigChange(ConfigChangeEvent event) {
        updateLogLevel(GlobalConfig.getConfig().getDebug());
    }
    
    public void updateLogLevel(boolean isDebug) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(BASE_PACKAGE);
        
        Level newLevel = isDebug ? Level.DEBUG : Level.INFO;
        logger.setLevel(newLevel);
        
        log.info("Log level has been set to: {}", newLevel);
    }
} 