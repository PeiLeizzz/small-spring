package com.peilei.springframework.aop.advisor;

import com.peilei.springframework.aop.aspectj.Pointcut;

/**
 * 获取 Advice 的切面的接口
 */
public interface PointcutAdvisor extends Advisor {
    /**
     * 获取 Advice 的切面
     * @return
     */
    Pointcut getPointcut();
}
