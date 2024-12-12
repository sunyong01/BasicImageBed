package web.sy.base.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import web.sy.base.event.ConfigChangeEvent;

import java.util.List;

@Slf4j
@Configuration
public class LoggingConfig {

    private static final List<String> PACKAGES = List.of(
        "web.sy.bed",
        "web.sy.base",
        "web.sy.storage"
    );

    @EventListener
    public void onConfigChange(ConfigChangeEvent event) {
        updateLogLevel(GlobalConfig.getConfig().getDebug());
    }

    public void updateLogLevel(boolean isDebug) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Level newLevel = isDebug ? Level.DEBUG : Level.INFO;

        // 更新根logger
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(newLevel);
        log.info("Root logger level has been set to: {}", newLevel);

        // 更新所有包的logger
        for (String packageName : PACKAGES) {
            Logger logger = loggerContext.getLogger(packageName);
            logger.setLevel(newLevel);
            log.info("Logger for package '{}' level has been set to: {}", packageName, newLevel);
        }

        // 获取所有logger并更新
        loggerContext.getLoggerList().forEach(logger -> {
            if (!logger.getName().equals(Logger.ROOT_LOGGER_NAME)) {
                logger.setLevel(newLevel);
                log.debug("Logger '{}' level has been set to: {}", logger.getName(), newLevel);
            }
        });

        log.info("All loggers have been updated to level: {}", newLevel);
    }
} 