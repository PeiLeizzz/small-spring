package com.peilei.springframework.context;

import com.peilei.springframework.beans.factory.ListableBeanFactory;

/**
 * 应用上下文的基接口
 * 应用上下文主要是用于管理 BeanDefinition 的加载和注册、Processor 的注册和提前实例化（单例）
 * 继承 ListableBeanFactory 类是为了获取能够直接批量构造某个类及其派生类 Bean 的能力
 */
public interface ApplicationContext extends ListableBeanFactory {

}
