package com.peilei.springframework.aop.aspectj;


/**
 * 切点接口，用于获取 ClassFilter 和 MethodMatcher
 * 通过这两个类能够获取切点表达式相关的内容
 */
public interface Pointcut {
    /**
     * 获取类过滤器
     * @return
     */
    ClassFilter getClassFilter();

    /**
     * 获取方法匹配器
     * @return
     */
    MethodMatcher getMethodMatcher();
}
