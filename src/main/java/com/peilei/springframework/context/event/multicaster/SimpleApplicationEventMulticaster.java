package com.peilei.springframework.context.event.multicaster;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.BeanFactory;
import com.peilei.springframework.context.event.ApplicationEvent;
import com.peilei.springframework.context.event.listener.ApplicationListener;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {
    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) throws BeansException {
        setBeanFactory(beanFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void multicastEvent(final ApplicationEvent event) throws BeansException {
        for (final ApplicationListener listener: getApplicationListeners(event)) {
            listener.onApplicationEvent(event);
        }
    }
}
