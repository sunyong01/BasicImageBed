package web.sy.bed.base.config.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import web.sy.bed.base.config.GlobalConfig;
import web.sy.bed.base.utils.SQLiteUtils;

@Slf4j
public class DebugModeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String debug =  SQLiteUtils.getValue("system.debug");
        if (debug == null) {
            log.warn("debug is null");
            return false;
        }
        return Boolean.parseBoolean(debug);
    }
}
