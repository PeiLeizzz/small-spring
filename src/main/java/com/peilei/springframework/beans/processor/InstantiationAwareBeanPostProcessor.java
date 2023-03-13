package com.peilei.springframework.beans.processor;

import com.peilei.springframework.beans.exception.BeansException;

/**
 * beanPostProcessor 的扩展，提供在 Bean 对象实例化之前执行的操作
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 定义在 Bean 对象实例化之前执行的操作
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;
}
