package com.peilei.springframework.beans.processor;

/**
 * Bean 初始化方法接口，在属性填充后调用
 */
public interface InitializingBean {
    /**
     * Bean 处理了属性填充后调用
     * @throws Exception
     */
    void afterPropertiesSet() throws Exception;
}
