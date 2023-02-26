package com.peilei.springframework.context.event.listener;

import com.peilei.springframework.context.event.ApplicationEvent;

import java.util.EventListener;

/**
 * 应用的监听器接口
 * @param <E> 继承于应用事件
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);
}
