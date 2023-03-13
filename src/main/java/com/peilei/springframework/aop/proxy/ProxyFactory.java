package com.peilei.springframework.aop.proxy;

import com.peilei.springframework.aop.combine.AdvisedSupport;

/**
 * 代理工厂
 * 按需创建不同的代理对象
 */
public class ProxyFactory {
    private AdvisedSupport advised;

    public ProxyFactory(AdvisedSupport advised) {
        this.advised = advised;
    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    private AopProxy createAopProxy() {
        if (advised.isProxyTargetClass()) {
            return new Cglib2AopProxy(advised);
        }
        return new JdkDynamicAopProxy(advised);
    }
}
