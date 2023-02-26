package com.peilei.springframework.context.processor;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.processor.BeanPostProcessor;
import com.peilei.springframework.context.ApplicationContext;
import com.peilei.springframework.context.aware.ApplicationContextAware;

/**
 * 包装处理器
 * 由于 ApplicationContext 的获取并不能直接在 createBean 时就拿到
 * 所以需要在 refresh 操作时，把 ApplicationContext 写入到一个包装的 BeanPostProcessor 中去
 * 再由 AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization 方法调用
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
