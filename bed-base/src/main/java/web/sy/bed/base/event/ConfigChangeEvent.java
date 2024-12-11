package web.sy.bed.base.event;

import org.springframework.context.ApplicationEvent;

public class ConfigChangeEvent extends ApplicationEvent {
    public ConfigChangeEvent(Object source) {
        super(source);
    }
} 