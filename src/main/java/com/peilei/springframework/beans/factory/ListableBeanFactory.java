package com.peilei.springframework.beans.factory;

import com.peilei.springframework.beans.exception.BeansException;

import java.util.Map;

/**
 * 扩展 Bean 工厂的接口
 * 扩展了两个方法：
 *  1. 根据 Class 对象返回当前容器中存放的 Bean 实例集合，以 {beanName: beanObj} 的形式返回
 *  2. 返回注册表中所有的 Bean 名称
 */
public interface ListableBeanFactory extends BeanFactory {
    /**
     * 根据 Class 对象构造并返回符合的 Bean 实例集合，包括其派生类
     * @param type
     * @return
     * @param <T>
     * @throws BeansException
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     * 返回注册表中所有的 Bean 名称
     * @return
     */
    String[] getBeanDefinitionNames();
}
