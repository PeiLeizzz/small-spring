package com.peilei.springframework.beans.processor;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.ConfigurableListableBeanFactory;

/**
 * 在所有的 BeanDefinition 加载完成后、Bean 对象实例化之前，提供修改 BeanDefinition 属性的机制
 */
public interface BeanFactoryPostProcessor {
    /**
     * 用于处理 BeanFactory 中的 BeanDefinition 对象的属性
     * 可以获取到注册表中所有的 BeanDefinition 对象
     * @param beanFactory
     * @throws BeansException
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
