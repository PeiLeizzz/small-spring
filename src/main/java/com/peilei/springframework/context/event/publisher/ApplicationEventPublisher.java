package com.peilei.springframework.context.event.publisher;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.context.event.ApplicationEvent;

/**
 * 事件的发布接口，所有的事件都要从这个接口发布出去
 */
public interface ApplicationEventPublisher {
    /**
     * 发布事件
     * @param event
     */
    void publishEvent(ApplicationEvent event) throws BeansException;
}
