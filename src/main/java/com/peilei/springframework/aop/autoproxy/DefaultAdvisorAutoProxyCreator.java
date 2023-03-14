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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 融入 Bean 生命周期的自动代理创建类
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    private DefaultListableBeanFactory beanFactory;

    /**
     * 其他 Bean 调用创建该 Bean 时，加入该缓存（防止多次调用 wrapIfNecessary 方法多次构建动态代理）
     */
    private final Set<Object> earlyProxyReferences = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Boolean postProcessAfterInstantiation(Object Bean, String beanName) throws BeansException {
        return true;
    }

    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 只有已经进入了三级缓存（已调用了 createBean），在 AbstractAutowiredCapableBeanFactory#doCreateBean 中的调用 getSingleton()
     * 才会进入三级缓存的 getObject() --> AbstractAutowiredCapableBeanFactory#getEarlyBeanReference
     * --> 这里的 getEarlyBeanReference() --> 创建代理对象
     *
     * 对象初始化完，才会进入 AbstractAutowiredCapableBeanFactory#applyBeanPostProcessorsAfterInitialization
     * --> 这里的 postProcessAfterInitialization
     *
     * 如果 先调用了这里的 getEarlyBeanReference() 后调用 postProcessAfterInitialization()
     * 只有一种可能，就是其他 bean 先初始化，然后填充属性时初始化了这个 bean（在三级缓存中）
     * A createBean -- B createBean -- B getSingleton A -- A getEarlyBeanReference -- B finish -- A postProcessAfterInitialization
     *
     * 这个 bean 初始化完（创建好了代理对象），通过 earlyProxyReference 来缓存
     * 当轮到它自己初始化完，要进行 postProcessAfterInitialization 操作时，直接从缓存中拿之前创建好的代理对象即可
     *
     * 总的流程：
     * A createBean --> A addSingletonFactory --> A 进入三级缓存 --> A apply property -->
     * B createBean --> B addSingletonFactory --> B 进入三级缓存 --> B apply property -->
     * B getSingleton of A --> A getEarlyBeanReference --> 从三级缓存获取到了 A 的代理对象 -->
     * A 从三级缓存进入二级缓存, A 代理对象进入 earlyProxyReferences 缓存 --> B 属性赋值完成 -->
     * B postProcessAfterInitialization --> 构建了 B 的代理对象 -->
     * B getSingleton --> B getEarlyBeanReference --> 构建了 B 的代理对象  --> B 代理对象进入 earlyProxyReferences 缓存
     * B 从三级缓存进入二级缓存 --> B registerSingleton --> B 从二级缓存进入一级缓存 --> B 结束
     * A 构造完成 --> A postProcessAfterInitialization --> A 从 earlyProxyReferences 获取到缓存的 A 代理对象
     * A getSingleton --> A registerSingleton --> A 从二级缓存进入一级缓存 --> A 结束
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!earlyProxyReferences.contains(beanName)) {
            return wrapIfNecessary(bean, beanName);
        }
        return bean;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return null;
    }

    /**
     * 获取代理对象
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        if (!earlyProxyReferences.contains(beanName)) {
            return wrapIfNecessary(bean, beanName);
        }
        return bean;
    }

    /**
     * 创建代理对象
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    protected Object wrapIfNecessary(Object bean, String beanName) throws BeansException {
        // 加入缓存，下次不会再调用该函数
        earlyProxyReferences.add(beanName);
        // 切面及切面方法本身不经过这个逻辑来初始化
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }

        // 先初始化所有的 PointcutAdvisor 及其子类
        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            // 满足表达式的类才创建代理对象
            if (!classFilter.matches(bean.getClass())) {
                continue;
            }

            AdvisedSupport advisedSupport = new AdvisedSupport();

            TargetSource targetSource = new TargetSource(bean);
            advisedSupport.setTargetSource(targetSource);
            if (advisor.getAdvice() instanceof MethodInterceptor) {
                advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
            }
            advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
            advisedSupport.setProxyTargetClass(true);

            return new ProxyFactory(advisedSupport).getProxy();
        }
        return bean;
    }
}
