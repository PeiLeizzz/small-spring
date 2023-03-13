package com.peilei.springframework.aop.autoproxy;

import com.peilei.springframework.aop.advisor.Advisor;
import com.peilei.springframework.aop.advisor.AspectJExpressionPointcutAdvisor;
import com.peilei.springframework.aop.aspectj.ClassFilter;
import com.peilei.springframework.aop.aspectj.Pointcut;
import com.peilei.springframework.aop.combine.AdvisedSupport;
import com.peilei.springframework.aop.combine.TargetSource;
import com.peilei.springframework.aop.proxy.ProxyFactory;
import com.peilei.springframework.beans.aware.BeanFactoryAware;
import com.peilei.springframework.beans.definition.PropertyValues;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.BeanFactory;
import com.peilei.springframework.beans.factory.DefaultListableBeanFactory;

import com.peilei.springframework.beans.processor.InstantiationAwareBeanPostProcessor;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collection;

/**
 * 融入 Bean 生命周期的自动代理创建类
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }


    /**
     * 创建代理对象
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        // 切面及切面方法本身不经过这个逻辑来初始化
        if (isInfrastructureClass(beanClass)) return null;

        // 先初始化所有的 PointcutAdvisor 及其子类
        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            // 满足表达式的类才创建代理对象
            if (!classFilter.matches(beanClass)) continue;

            AdvisedSupport advisedSupport = new AdvisedSupport();

            TargetSource targetSource = null;
            try {
                targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }

            advisedSupport.setTargetSource(targetSource);
            if (advisor.getAdvice() instanceof MethodInterceptor) {
                advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
            }
            advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
            advisedSupport.setProxyTargetClass(false);

            return new ProxyFactory(advisedSupport).getProxy();
        }

        return null;
    }

    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return null;
    }
}
