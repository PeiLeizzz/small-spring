package com.peilei.springframework.aop.proxy;

import com.peilei.springframework.aop.combine.AdvisedSupport;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于 JDK 实现的动态代理类
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), advised.getTargetSource().getTargetClass(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args);

        if (advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
            return advised.getMethodInterceptor().invoke(methodInvocation);
        }
        return methodInvocation.proceed();
    }
}
