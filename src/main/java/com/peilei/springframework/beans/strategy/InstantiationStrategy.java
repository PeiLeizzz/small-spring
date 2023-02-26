package com.peilei.springframework.beans.strategy;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.definition.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * 实例化策略接口
 */
public interface InstantiationStrategy {
    /**
     * constructor 为 null 时，调用无参构造
     * @param beanDefinition
     * @param beanName
     * @param constructor
     * @param args
     * @return
     * @throws BeansException
     */
    Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args) throws BeansException;
}
