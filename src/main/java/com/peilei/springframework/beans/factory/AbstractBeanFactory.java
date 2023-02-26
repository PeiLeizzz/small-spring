package com.peilei.springframework.beans.factory;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.processor.BeanPostProcessor;
import com.peilei.springframework.beans.registry.DefaultSingletonBeanRegistry;
import com.peilei.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义了获取 Bean 对象的步骤
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {
    /**
     * BeanPostProcessor 处理器集合
     */
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    /**
     * 该 Bean 工厂中的类加载器
     */
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /**
     * 根据名称获取 Bean 对象（无参）
     * @param name Bean 名称
     * @return
     * @throws BeansException
     */
    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null);
    }

    /**
     * 根据名称实例化并获取 Bean 对象（有参）
     * @param name Bean 名称
     * @return
     * @throws BeansException
     */
    @Override
    public Object getBean(String name, Object ...args) throws BeansException {
        return doGetBean(name, args);
    }

    /**
     * 根据 Bean 名称和类型直接返回对应类型的 Bean 对象（无参）
     * @param name
     * @param requiredType
     * @return
     * @param <T>
     * @throws BeansException
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return (T) getBean(name);
    }

    /**
     * 根据 Bean 名称和参数构建 Bean 对象的核心逻辑
     * @param name
     * @param args
     * @return
     * @param <T>
     */
    protected <T> T doGetBean(String name, Object[] args) throws BeansException {
        // 先尝试获取单例对象
        // 如果不是新的 name，之前会有缓存
        Object bean = getSingleton(name);
        if (bean != null) {
            return (T) bean;
        }

        // 获取 Bean 定义对象
        BeanDefinition beanDefinition = getBeanDefinition(name);
        // 实例化 Bean 对象并填充其属性
        return (T) createBean(name, beanDefinition, args);
    }

    /**
     * 根据 Bean 名称获取 BeanDefinition 对象
     * @param beanName
     * @return
     * @throws BeansException
     */
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * 实例化 Bean 对象并填充其属性
     * @param beanName
     * @param beanDefinition
     * @return
     * @throws BeansException
     */
    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
}
