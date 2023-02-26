package com.peilei.springframework.context.event;

import com.peilei.springframework.context.ApplicationContext;

/**
 * 应用上下文事件类，所有事件都需要继承这个类
 */
public class ApplicationContextEvent extends ApplicationEvent {
    public ApplicationContextEvent(Object source) {
        super(source);
    }

    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }
}
