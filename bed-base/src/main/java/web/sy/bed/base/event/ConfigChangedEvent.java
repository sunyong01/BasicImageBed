package web.sy.bed.base.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ConfigChangedEvent extends ApplicationEvent {
    private final String key;
    private final String value;

    public ConfigChangedEvent(Object source, String key, String value) {
        super(source);
        this.key = key;
        this.value = value;
    }
} 