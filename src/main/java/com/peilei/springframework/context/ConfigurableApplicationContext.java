package com.peilei.springframework.context;

import com.peilei.springframework.beans.exception.BeansException;

/**
 * 定义了刷新上下文中的 IoC 容器接口
 */
public interface ConfigurableApplicationContext extends ApplicationContext {
    /**
     * 刷新容器
     * @throws BeansException
     */
    void refresh() throws BeansException;

    /**
     * 注册关闭方法的虚拟机钩子
     */
    void registerShutdownHook();

    /**
     * 关闭上下文，执行注册表中 Bean 的销毁方法
     */
    void close();
}
