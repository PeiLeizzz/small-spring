package com.peilei.springframework.context;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.peilei.springframework.beans.factory.DefaultListableBeanFactory;

/**
 * 实现了创建 Bean 工厂逻辑的抽象类
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    private DefaultListableBeanFactory beanFactory;

    @Override
    protected void refreshBeanFactory() throws BeansException {
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        // 加载指定 BeanFactory 的 BeanDefinition 注册表的具体逻辑
        // 所以这里关联的 BeanFactory 对象必须又有管理注册表的能力（DefaultListableBeanFactory 及其子类）
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }

    private DefaultListableBeanFactory createBeanFactory() throws BeansException {
        return new DefaultListableBeanFactory();
    }

    /**
     * 加载 Bean 对象定义的注册表
     * @param beanFactory
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException;

    @Override
    protected ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
