package com.peilei.springframework.context.event.multicaster;

import com.peilei.springframework.beans.aware.BeanFactoryAware;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.BeanFactory;
import com.peilei.springframework.context.event.ApplicationEvent;
import com.peilei.springframework.context.event.listener.ApplicationListener;
import com.peilei.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 提取事件广播器公用方法的抽象类
 */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {
    public final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new LinkedHashSet<>();

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }

    /**
     * 获取所有支持处理该 event 的 listener 集合
     * @param event
     * @return
     */
    protected Collection<ApplicationListener> getApplicationListeners(ApplicationEvent event) throws BeansException {
        LinkedList<ApplicationListener> allListeners = new LinkedList<>();
        for (ApplicationListener<ApplicationEvent> listener : applicationListeners) {
            if (supportsEvent(listener, event)) {
                allListeners.add(listener);
            }
        }
        return allListeners;
    }

    /**
     * 判断该 listener 是否支持处理该 event
     * 实际上就是检查 listener 的类型（ApplicationListener<ApplicationEvent>）中的泛型事件类是不是 event 的超类或者超接口
     * @param listener
     * @param event
     * @return
     * @throws BeansException
     */
    protected boolean supportsEvent(ApplicationListener<ApplicationEvent> listener, ApplicationEvent event) throws BeansException {
        Class<? extends ApplicationListener> listenerClass = listener.getClass();

        // 按照不同的实例化策略，需要判断后获取目标 class
        Class<?> targetClass = ClassUtils.isCglibProxyClass(listenerClass)? listenerClass.getSuperclass(): listenerClass;
        // 拿到泛型
        Type genericInterface = targetClass.getGenericInterfaces()[0];

        // 拿到泛型的实际类型
        Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
        String className = actualTypeArgument.getTypeName();
        Class<?> eventClassName;
        try {
            eventClassName = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeansException("wrong event class name: " + className);
        }
        // 即判断 listener 中泛型的实际类型是否为 event 的超类或超接口
        return eventClassName.isAssignableFrom(event.getClass());
    }
}
