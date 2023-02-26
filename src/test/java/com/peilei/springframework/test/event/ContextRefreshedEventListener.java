package com.peilei.springframework.test.event;

import com.peilei.springframework.context.event.ContextRefreshedEvent;
import com.peilei.springframework.context.event.listener.ApplicationListener;

public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("容器刷新了");
    }
}
