package com.peilei.springframework.test.event;

import com.peilei.springframework.context.event.ContextClosedEvent;
import com.peilei.springframework.context.event.listener.ApplicationListener;

public class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("容器销毁了");
    }
}
