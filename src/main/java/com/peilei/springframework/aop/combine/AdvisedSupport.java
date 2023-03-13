package com.peilei.springframework.aop.combine;

import com.peilei.springframework.aop.aspectj.MethodMatcher;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * 包装切面相关信息和工具的类
 * 把代理对象、拦截和匹配包装到一个类中
 * 方便在 Proxy 实现类中使用
 */
public class AdvisedSupport {
    // 被代理的目标对象的包装对象
    private TargetSource targetSource;

    // 方法拦截器
    // 由用户自己实现其 invoke 方法，做具体的处理
    private MethodInterceptor methodInterceptor;

    // 方法匹配器
    // 由 AspectJExpressionPointcut 提供服务
    private MethodMatcher methodMatcher;

    // ProxyConfig 默认为 false
    private boolean proxyTargetClass = false;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }
}
