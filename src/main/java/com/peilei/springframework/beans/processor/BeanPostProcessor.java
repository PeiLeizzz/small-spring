package com.peilei.springframework.beans.processor;

import com.peilei.springframework.beans.exception.BeansException;

/**
 * 在 Bean 对象实例化之后、执行初始化方法前后修改 Bean 对象的处理机制
 */
public interface BeanPostProcessor {
    /**
     * 初始化方法执行前的处理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 初始化方法执行后的处理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
