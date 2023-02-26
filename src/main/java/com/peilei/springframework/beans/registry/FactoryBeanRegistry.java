package com.peilei.springframework.beans.registry;


import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.definition.FactoryBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FactoryBean 对象的注册器
 */
public abstract class FactoryBeanRegistry extends DefaultSingletonBeanRegistry {
    /**
     * 存放单例对象的缓存
     */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    /**
     * 从 FactoryBean 中获取 Object 对象
     * @param factory
     * @param beanName
     * @return
     * @throws BeansException
     */
    protected Object getObjectFromFactoryBean(FactoryBean factory, String beanName) throws BeansException {
        // 非单例每次都需要创建
        if (!factory.isSingleton()) {
            return doGetObjectFromFactoryBean(factory, beanName);
        }

        Object object = this.factoryBeanObjectCache.get(beanName);
        if (object == null) {
            object = doGetObjectFromFactoryBean(factory, beanName);
            this.factoryBeanObjectCache.put(beanName, object == null? NULL_OBJECT: object);
        }
        return object == NULL_OBJECT ? null: object;
    }

    /**
     * 从缓存中获取 FactoryBean 的 Object 对象
     * @param beanName
     * @return
     */
    private Object getCachedObjectForFactoryBean(String beanName) {
        Object object = this.factoryBeanObjectCache.get(beanName);
        return object == NULL_OBJECT ? null: object;
    }

    /**
     * 从 factory.getObject() 中获取 Object 对象
     * @param factory
     * @param beanName
     * @return
     * @throws BeansException
     */
    private Object doGetObjectFromFactoryBean(final FactoryBean factory, final String beanName) throws BeansException {
        try {
            return factory.getObject();
        } catch (Exception e) {
            throw new BeansException("FactoryBean threw exception on object[" + beanName + "] creation", e);
        }
    }
}
