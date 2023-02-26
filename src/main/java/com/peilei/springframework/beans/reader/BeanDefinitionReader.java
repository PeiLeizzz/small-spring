package com.peilei.springframework.beans.reader;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.registry.BeanDefinitionRegistry;
import com.peilei.springframework.core.io.Resource;
import com.peilei.springframework.core.io.ResourceLoader;

/**
 * 读取 BeanDefinition 信息的接口
 */
public interface BeanDefinitionReader {
    /**
     * 给 loadBeanDefinitions 方法提供注册器，用于注册 BeanDefinition 对象集合
     * @return
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * 给 loadBeanDefinitions 方法提供资源加载器，用于从文件 / classpath / 网络中读取 BeanDefinition 信息
     * @return
     */
    ResourceLoader getResourceLoader();

    void loadBeanDefinitions(Resource resource) throws BeansException;

    void loadBeanDefinitions(Resource ...resources) throws BeansException;

    void loadBeanDefinitions(String location) throws BeansException;

    void loadBeanDefinitions(String ...locations) throws BeansException;
}
