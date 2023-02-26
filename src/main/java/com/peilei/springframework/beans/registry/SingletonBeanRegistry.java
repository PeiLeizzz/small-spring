package com.peilei.springframework.beans.registry;

/**
 * 获取单例对象接口
 */
public interface SingletonBeanRegistry {
    /**
     * 根据 Bean 名称获取容器中的单例对象
     * @param beanName
     * @return
     */
    Object getSingleton(String beanName);
}
