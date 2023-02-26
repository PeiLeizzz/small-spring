package com.peilei.springframework.beans.registry;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.processor.DisposableBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 单例对象的注册与获取，默认实现
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    /**
     * 单例对象集合
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 单例对象的销毁方法集合
     */
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    /**
     * 内置的空对象单例，因为 ConcurrentMap 不支持 null
     */
    protected static final Object NULL_OBJECT = new Object();

    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }

    /**
     * 注册单例对象，只有子类可调用
     * @param beanName
     * @param singletonObject
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    /**
     * 向销毁集合中注册 Bean 名称和其销毁方法
     * @param beanName
     * @param bean
     */
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

    /**
     * 销毁单例对象
     * 父类实现子类接口方法
     */
    public void destroySingletons() throws BeansException {
        Set<String> keySet = this.disposableBeans.keySet();
        Object[] disposableBeanNames = keySet.toArray();

        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            Object beanName = disposableBeanNames[i];
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '" + beanName + "' threw an exception", e);
            }
        }
    }
}
