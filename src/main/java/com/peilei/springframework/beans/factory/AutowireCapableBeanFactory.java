package com.peilei.springframework.beans.factory;

import com.peilei.springframework.beans.exception.BeansException;

/**
 * 自动化处理 Bean 装配的接口
 */
public interface AutowireCapableBeanFactory extends BeanFactory {
    /**
     * 对 Bean 对象在执行初始化方法之前先执行 before 处理器的流程
     * 让处理器集合中的所有处理器都对 Bean 对象进行处理
     * @param exisingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object exisingBean, String beanName) throws BeansException;

    /**
     * 对 Bean 对象在执行初始化方法之后执行 after 处理器的流程
     * 让处理器集合中的所有处理器都对 Bean 对象进行处理
     * @param exisingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object applyBeanPostProcessorsAfterInitialization(Object exisingBean, String beanName) throws BeansException;
}
