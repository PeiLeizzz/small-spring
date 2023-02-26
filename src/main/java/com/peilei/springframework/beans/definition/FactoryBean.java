package com.peilei.springframework.beans.definition;

/**
 * 对外提供获取工厂中 Bean object 对象的接口
 * @param <T>
 */
public interface FactoryBean<T> {
    /**
     * 获取 FactoryBean 的 object 对象
     * 可以通过这个方法包装 T 的代理对象
     * @return
     * @throws Exception
     */
    T getObject() throws Exception;

    /**
     * 获取 FactoryBean 的 object 对象类型
     * @return
     */
    Class<?> getObjectType();

    /**
     * 判断该 FactoryBean 中的 object 是否为单例，和 BeanDefinition 中的单例概念不同 <br/>
     * BeanDefinition 中的单例是指在 IoC 中是否要加入单例池，下次不用再调用 createBean() <br/>
     * FactoryBean 中的单例是指在 FactoryBeanRegistry 中是否要加入缓存，下次不用再调用 FactoryBean.getObject() <br/>
     * 这两个是单独检查的，即使 BeanDefinition 是单例，如果它实现了 FactoryBean 接口，仍然要通过该方法判断是否需要再次 getObject() <br/>
     * @return
     */
    boolean isSingleton();
}
