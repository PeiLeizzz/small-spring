package com.peilei.springframework.beans.factory;

import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.exception.BeansException;

/**
 * 提供分析和修改 Bean 以及预先实例化的操作
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {
    /**
     * 根据 Bean 名称获取 Bean 定义对象
     * @param beanName
     * @return
     * @throws BeansException
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * 预先实例化单例对象
     * @throws BeansException
     */
    void preInstantiateSingletons() throws BeansException;
}
