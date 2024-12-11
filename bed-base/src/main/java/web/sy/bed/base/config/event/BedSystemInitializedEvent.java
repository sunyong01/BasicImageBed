package web.sy.bed.base.config.event;

import org.springframework.context.ApplicationEvent;

public class BedSystemInitializedEvent extends ApplicationEvent {
    public BedSystemInitializedEvent(Object source) {
        super(source);
    }
} 