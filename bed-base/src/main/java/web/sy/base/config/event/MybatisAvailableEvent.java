package web.sy.base.config.event;

import org.springframework.context.ApplicationEvent;

public class MybatisAvailableEvent  extends ApplicationEvent {
    public MybatisAvailableEvent(Object source) {
        super(source);
    }
}
