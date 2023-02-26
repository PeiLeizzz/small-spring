package com.peilei.springframework.context.event;

/**
 * 上下文关闭事件类，用于监听关闭动作
 */
public class ContextClosedEvent extends ApplicationContextEvent {
    public ContextClosedEvent(Object source) {
        super(source);
    }
}
