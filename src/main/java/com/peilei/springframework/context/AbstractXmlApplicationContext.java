package com.peilei.springframework.context;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.DefaultListableBeanFactory;
import com.peilei.springframework.beans.reader.XmlBeanDefinitionReader;

/**
 * 实现了从指定 Bean 工厂和指定 location 中加载 BeanDefinition 注册表逻辑的抽象类
 * 没有实现如何获取 location
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {
    /**
     * 实现从执行 Bean 工厂和指定 location 中加载 BeanDefinition 注册表的逻辑
     * @param beanFactory
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException {
        // 继承了 DefaultResourceLoader
        // XML Bean 定义加载器
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);

        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            beanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    /**
     * 从入口上下文类拿到配置信息地址描述的接口
     * @return
     */
    protected abstract String[] getConfigLocations();
}
