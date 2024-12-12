package web.sy.base.config.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import web.sy.base.utils.SQLiteUtils;

@Slf4j
public class InitializedCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String initialized =  SQLiteUtils.getValue("system.initialized");
        if (initialized == null) {
            log.warn("initialized is null");
            return false;
        }
        return Boolean.parseBoolean(initialized);
    }
} 