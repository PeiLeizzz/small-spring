package com.peilei.springframework.aop.aspectj;

/**
 * 类过滤器接口，用于判断切点是否匹配给定的接口或目标类
 */
public interface ClassFilter {
    /**
     * 判断切点是否匹配给定的接口或目标类
     * @param clazz
     * @return
     */
    boolean matches(Class<?> clazz);
}
