package com.peilei.springframework.beans.registry;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.ObjectFactory;
import com.peilei.springframework.beans.processor.DisposableBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 单例对象的注册与获取，默认实现
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    /**
     * 单例对象一级缓存，普通对象
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 二级缓存，没有完全实例化的对象
     */
    protected final Map<String, Object> earlySingletonObjects = new HashMap<>();

    /**
     * 三级缓存，存放代理对象和工厂对象
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();

    /**
     * 单例对象的销毁方法集合
     */
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    /**
     * 内置的空对象单例，因为 ConcurrentMap 不支持 null
     */
    protected static final Object NULL_OBJECT = new Object();

    @Override
    public Object getSingleton(String beanName) throws BeansException {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject != null) {
            return singletonObject;
        }

        singletonObject = earlySingletonObjects.get(beanName);
        if (singletonObject != null) {
            return singletonObject;
        }

        // 代理对象 / 工厂对象
        ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
        if (singletonFactory != null) {
            singletonObject = singletonFactory.getObject();
            // 把三级缓存中的代理 / 工厂对象中的真实对象获取出来，放入二级缓存中
            earlySingletonObjects.put(beanName, singletonObject);
            singletonFactories.remove(beanName);
        }
        return singletonObject;
    }

    /**
     * 注册单例对象
     * @param beanName
     * @param singletonObject
     */
    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    /**
     * 注册代理对象 / 工厂对象
     * @param beanName
     * @param singletonFactory
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        if (!this.singletonObjects.containsKey(beanName)) {
            this.singletonFactories.put(beanName, singletonFactory);
            this.earlySingletonObjects.remove(beanName);
        }
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
