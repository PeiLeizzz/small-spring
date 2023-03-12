package com.peilei.springframework.aop.proxy;

/**
 * AOP 代理的接口
 */
public interface AopProxy {
    /**
     * 获取代理对象
     * @return
     */
    Object getProxy();
}
