package com.peilei.springframework.aop.aspectj;

import java.lang.reflect.Method;

/**
 * 方法匹配器接口，用于判断切点是否匹配目标类给定的方法
 */
public interface MethodMatcher {
    /**
     * 用于判断切点是否匹配目标类给定的方法
     * @param method
     * @param targetClass
     * @return
     */
    boolean matches(Method method, Class<?> targetClass);
}
