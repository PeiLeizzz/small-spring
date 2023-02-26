package com.peilei.springframework.context;

import com.peilei.springframework.beans.exception.BeansException;

/**
 * 补充了如何获取配置文件 location
 * 同时表示该类用于读取 classpath 中的 xml 文件
 * 在构造函数中会直接上下文环境中的容器
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {
    private String[] configLocations;

    public ClassPathXmlApplicationContext() {}

    public ClassPathXmlApplicationContext(String configLocations) throws BeansException {
        this(new String[] {configLocations});
    }

    public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        this.configLocations = configLocations;
        // 直接刷新上下文环境中的容器
        refresh();
    }

    @Override
    protected String[] getConfigLocations() {
        return configLocations;
    }
}
