package com.peilei.springframework.context.event;

/**
 * 上下文刷新事件类，用于监听刷新动作
 */
public class ContextRefreshedEvent extends ApplicationContextEvent {
    public ContextRefreshedEvent(Object source) {
        super(source);
    }
}
