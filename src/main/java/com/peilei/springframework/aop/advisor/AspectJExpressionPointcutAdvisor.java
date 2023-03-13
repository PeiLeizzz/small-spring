package com.peilei.springframework.aop.advisor;

import com.peilei.springframework.aop.aspectj.AspectJExpressionPointCut;
import com.peilei.springframework.aop.aspectj.Pointcut;
import org.aopalliance.aop.Advice;

/**
 * 将切面、拦截方法、拦截表达式包装在一起
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {
    // 切面
    private AspectJExpressionPointCut pointCut;

    // 具体的拦截方法
    private Advice advice;

    // 表达式
    private String expression;

    public AspectJExpressionPointcutAdvisor() { }

    public AspectJExpressionPointcutAdvisor(String expression) {
        this.expression = expression;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }


    @Override
    public Pointcut getPointcut() {
        if (pointCut == null) {
            pointCut = new AspectJExpressionPointCut(expression);
        }
        return pointCut;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }
}
