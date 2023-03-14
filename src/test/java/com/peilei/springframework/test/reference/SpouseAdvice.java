package com.peilei.springframework.test.reference;

import com.peilei.springframework.aop.advice.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class SpouseAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("advice: " + method);
    }
}
