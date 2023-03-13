package com.peilei.springframework.aop.advice;

import java.lang.reflect.Method;

/**
 * 定义切面前行为的拦截器接口
 */
public interface MethodBeforeAdvice extends BeforeAdvice {
    /**
     * 定义切面前的行为
     * @param method
     * @param args
     * @param target
     * @throws Throwable
     */
    void before(Method method, Object[] args, Object target) throws Throwable;
}
