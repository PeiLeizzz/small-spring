package com.peilei.springframework.context;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.peilei.springframework.beans.processor.BeanFactoryPostProcessor;
import com.peilei.springframework.beans.processor.BeanPostProcessor;
import com.peilei.springframework.context.event.ApplicationEvent;
import com.peilei.springframework.context.event.ContextClosedEvent;
import com.peilei.springframework.context.event.ContextRefreshedEvent;
import com.peilei.springframework.context.event.listener.ApplicationListener;
import com.peilei.springframework.context.event.multicaster.ApplicationEventMulticaster;
import com.peilei.springframework.context.event.multicaster.SimpleApplicationEventMulticaster;
import com.peilei.springframework.context.processor.ApplicationContextAwareProcessor;
import com.peilei.springframework.core.io.DefaultResourceLoader;

import java.util.Collection;
import java.util.Map;

/**
 * 定义了刷新容器步骤的上下文环境抽象类
 * 包含了通过指定 Bean 工厂加载并执行 BeanFactoryPostProcessor、注册 BeanPostProcessor 的逻辑
 * 同时继承了 DefaultResourceLoader，因此具有加载 Resource 的能力
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

    private ApplicationEventMulticaster applicationEventMulticaster;

    @Override
    public void refresh() throws BeansException {
        // 创建 BeanFactory 并加载 BeanDefinition
        // 在这步中会 load 所有 reader 中获取的 BeanDefinition
        // 包括所有的 processor 的 BeanDefinition
        // 用于 Bean 对象实例化、初始化的流程中
        refreshBeanFactory();

        // 获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        // 添加 ApplicationContextAwareProcessor
        // 让继承自 ApplicationContextAware 的 Bean 对象都能感知所属的 ApplicationContext
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

        // Bean 实例化之前的处理，执行 BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessors(beanFactory);

        // 在 Bean 对象实例化之前，先注册实例化前后的处理器
        registerBeanPostProcessors(beanFactory);

        // 初始化事件发布者
        initApplicationEventMulticaster();

        // 注册事件监听器
        registerListeners();

        // 预先实例化单例对象
        // xml 中没有 scope 字段的默认都是单例对象
        beanFactory.preInstantiateSingletons();

        // 发布容器刷新完成事件
        finishRefresh();
    }

    /**
     * 创建 BeanFactory 并加载 BeanDefinition 注册表
     * 这里的 BeanFactory 应该是 getBeanFactory() 返回的
     * 在这步中应加载所有的 processor 的 BeanDefinition
     * 用于 Bean 对象实例化、初始化的流程中
     * @throws BeansException
     */
    protected abstract void refreshBeanFactory() throws BeansException;

    /**
     * 获取 Bean 工厂
     * 这里的工厂必须可以处理 BeanPostProcessor
     * 同时也可以被 BeanFactoryPostProcessor 处理（ConfigurableListableBeanFactory）
     * @return
     */
    protected abstract ConfigurableListableBeanFactory getBeanFactory();

    /**
     * 执行所有 BeanDefinition 加载完之后、Bean 对象实例化之前的处理机制
     * @param beanFactory
     */
    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 先构造好并获取到所有的 BeanFactoryPostProcessor
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);

        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    /**
     * 注册所有的 BeanPostProcessors
     * @param beanFactory
     */
    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 先构造并获取到所有的 BeanPostProcessor
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    /**
     * 初始化事件发布者
     * @throws BeansException
     */
    private void initApplicationEventMulticaster() throws BeansException {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        // 加入单例池
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
    }

    /**
     * 从配置文件中获取并注册事件监听器
     * @throws BeansException
     */
    private void registerListeners() throws BeansException {
        Collection<ApplicationListener> applicationListeners = getBeansOfType(ApplicationListener.class).values();
        for (ApplicationListener listener: applicationListeners) {
            applicationEventMulticaster.addApplicationListener(listener);
        }
    }

    /**
     * 发布容器刷新事件
     */
    private void finishRefresh() throws BeansException {
        publishEvent(new ContextRefreshedEvent(this));
    }

    /**
     * 发布事件
     * @param event
     */
    @Override
    public void publishEvent(ApplicationEvent event) throws BeansException {
        applicationEventMulticaster.multicastEvent(event);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return getBeanFactory().getBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void close() {
        try {
            // 发布容器关闭事件
            publishEvent(new ContextClosedEvent(this));
            // 执行销毁单例 bean 的销毁方法
            getBeanFactory().destroySingletons();
        } catch (BeansException e) {
            System.out.println(e);
        }
    }
}
