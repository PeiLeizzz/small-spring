package com.peilei.springframework.beans.processor;

import com.peilei.springframework.beans.definition.PropertyValues;
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

    /**
     * 在 Bean 对象实例化之后、设置属性之前执行该方法
     * @param pvs
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException;
}
