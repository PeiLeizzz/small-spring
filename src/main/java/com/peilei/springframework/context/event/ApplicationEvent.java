package com.peilei.springframework.context.event;

import java.util.EventObject;

/**
 * 应用中所有事件的抽象类
 */
public abstract class ApplicationEvent extends EventObject {
    /**
     * @param source 事件源
     */
    public ApplicationEvent(Object source) {
        super(source);
    }
}
