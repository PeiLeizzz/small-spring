package com.peilei.springframework.beans.registry;

import com.peilei.springframework.beans.definition.BeanDefinition;

/**
 * BeanDefinition 注册接口
 */
public interface BeanDefinitionRegistry {
    /**
     * 向注册表中注册 BeanDefinition
     * @param beanName
     * @param beanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 判断是否包含指定名称的 BeanDefinition
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);
}
