package com.peilei.springframework.aop.advisor;

import org.aopalliance.aop.Advice;

/**
 * 获取拦截方法接口
 */
public interface Advisor {
    /**
     * 获取拦截方法，可以是 Interceptor / beforeAdvice / throwsAdvice etc.
     * @return
     */
    Advice getAdvice();
}
