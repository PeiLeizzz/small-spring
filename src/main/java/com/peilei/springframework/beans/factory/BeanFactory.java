package com.peilei.springframework.beans.factory;

import com.peilei.springframework.beans.exception.BeansException;

/**
 * Bean 工厂接口，为最基础的根接口
 * 职责为根据 Bean 名称获取 Bean 对象
 */
public interface BeanFactory {
    /**
     * 根据 Bean 名称获取 Bean 对象
     * @param name Bean 名称
     * @return Bean 对象
     */
    Object getBean(String name) throws BeansException;

    /**
     * 根据 Bean 名称获取 Bean 对象（有参）
     * @param name
     * @param args
     * @return
     * @throws BeansException
     */
    Object getBean(String name, Object ...args) throws BeansException;

    /**
     * 根据 Bean 名称和类型直接返回对应类型的 Bean 对象（无参）
     * @param name
     * @param requiredType
     * @return
     * @param <T>
     * @throws BeansException
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    /**
     * 根据 Bean 类型直接返回 Bean 对象（无参）
     * @param requiredType
     * @return
     * @param <T>
     * @throws BeansException
     */
    <T> T getBean(Class<T> requiredType) throws BeansException;
}
