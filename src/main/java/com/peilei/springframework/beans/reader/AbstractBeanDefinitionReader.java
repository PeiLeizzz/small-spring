package com.peilei.springframework.beans.reader;

import com.peilei.springframework.beans.registry.BeanDefinitionRegistry;
import com.peilei.springframework.core.io.DefaultResourceLoader;
import com.peilei.springframework.core.io.ResourceLoader;

/**
 * 加载 BeanDefinition 信息的抽象类
 * 实现了两个成员的获取方法
 * 同时提供构造函数给外部，可以传入指定成员
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    private final BeanDefinitionRegistry registry;

    private ResourceLoader resourceLoader;

    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
