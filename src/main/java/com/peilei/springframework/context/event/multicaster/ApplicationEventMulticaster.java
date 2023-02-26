package com.peilei.springframework.context.event.multicaster;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.context.event.ApplicationEvent;
import com.peilei.springframework.context.event.listener.ApplicationListener;

/**
 * 应用事件广播器
 */
public interface ApplicationEventMulticaster {
    /**
     * 添加监听器
     * @param listener
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * 移除监听器
     * @param listener
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * 对事件进行广播
     * @param event
     */
    void multicastEvent(ApplicationEvent event) throws BeansException;
}
