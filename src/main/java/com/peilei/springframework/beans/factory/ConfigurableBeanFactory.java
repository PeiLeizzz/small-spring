package com.peilei.springframework.beans.factory;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.processor.BeanPostProcessor;
import com.peilei.springframework.beans.registry.SingletonBeanRegistry;
import com.peilei.springframework.util.StringValueResolver;

/**
 * 可获取 BeanPostProcessor、BeanClassLoader 等的一个配置化接口
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * 向 BeanPostProcessor 处理器集合中添加一个 BeanPostProcessor
     * @param beanPostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 销毁单例对象
     */
    void destroySingletons() throws BeansException;

    /**
     * 添加字符串处理器
     * @param resolver
     */
    void addEmbeddedValueResolver(StringValueResolver resolver);

    /**
     * 用字符串处理器解析字符串
     * @param value
     */
    String resolveEmbeddedValue(String value);
}
