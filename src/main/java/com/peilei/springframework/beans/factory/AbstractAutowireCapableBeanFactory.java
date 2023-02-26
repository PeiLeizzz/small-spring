package com.peilei.springframework.beans.factory;

import cn.hutool.core.bean.BeanUtil;
import com.peilei.springframework.beans.adapter.DisposableBeanAdapter;
import com.peilei.springframework.beans.adapter.InitializingBeanAdapter;
import com.peilei.springframework.beans.aware.Aware;
import com.peilei.springframework.beans.aware.BeanClassLoaderAware;
import com.peilei.springframework.beans.aware.BeanFactoryAware;
import com.peilei.springframework.beans.aware.BeanNameAware;
import com.peilei.springframework.beans.definition.BeanReference;
import com.peilei.springframework.beans.definition.PropertyValue;
import com.peilei.springframework.beans.definition.PropertyValues;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.processor.BeanPostProcessor;
import com.peilei.springframework.beans.strategy.CglibSubclassingInstantiationStrategy;
import com.peilei.springframework.beans.strategy.InstantiationStrategy;

import java.lang.reflect.Constructor;

/**
 * 只关心 Bean 对象的实例化具体逻辑（自动装配属性）
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    /**
     * 对象的实例化策略（JDK / CGlib）
     */
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    /**
     * 实例化 Bean 对象并填充其属性的具体逻辑实现
     * @param beanName
     * @param beanDefinition
     * @param args
     * @return
     * @throws BeansException
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            // 通过构造函数实例化 Bean
            bean = createBeanInstance(beanDefinition, beanName, args);
            // 根据 Bean 定义对象中的属性值集合，填充 Bean 的属性值，会覆盖上面构造函数设置的值
            applyPropertyValues(beanName, bean, beanDefinition);
            // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        // 注册实现了 DisposableBean 接口的 Bean 对象
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        // 单例对象加入单例池
        if (beanDefinition.isSingleton()) {
            addSingleton(beanName, bean);
        }
        return bean;
    }

    /**
     * 根据 Bean 的定义和构造函数的参数，实例化 Bean 对象
     * @param beanDefinition
     * @param beanName
     * @param args
     * @return
     */
    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) throws BeansException {
        Constructor constructorToUse = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 所有 public 的构造函数
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();

        for (Constructor constructor : declaredConstructors) {
            // 找到和参数数量一致的构造函数
            // TODO：参数类型校验
            if (args != null && constructor.getParameterTypes().length == args.length) {
                constructorToUse = constructor;
                break;
            }
        }

        // 当 args 为 null / 长度为 0 时，constructorToUse = null
        // 此时会调用无参构造
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    /**
     * 根据 Bean 定义对象中的属性值集合，填充 Bean 对象的属性值
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) throws BeansException {
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            for (PropertyValue pv : propertyValues.getPropertyValues()) {
                String name = pv.getName();
                Object value = pv.getValue();

                // 引用类型对象递归实例化与属性填充
                if (value instanceof BeanReference) {
                    BeanReference beanReference = (BeanReference) value;
                    // TODO：构造参数？
                    value = getBean(beanReference.getBeanName());
                }
                // 设置成员的值
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception e) {
            throw new BeansException("Error setting property values: " + beanName);
        }
    }

    /**
     * Bean 的初始化行为，beforeProcess -- initMethod -- afterProcess
     * @param beanName
     * @param bean
     * @param beanDefinition
     * @return
     */
    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) throws BeansException {
        // 配置感知器
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
        }

        // 执行 BeanPostProcessor Before 处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 执行 Bean 对象的初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Invocation of init method of bean[" + beanName + "] failed", e);
        }

        wrappedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        return wrappedBean;
    }

    /**
     * Bean 的初始化函数
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        new InitializingBeanAdapter(bean, beanName, beanDefinition).afterPropertiesSet();
    }

    /**
     * 注册 Bean 的销毁函数到虚拟机钩子中
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 非 Singleton 类型的 Bean 不执行销毁方法
        if (!beanDefinition.isSingleton()) {
            return;
        }
        registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object exisingBean, String beanName) throws BeansException {
        Object result = exisingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);

            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object exisingBean, String beanName) throws BeansException {
        Object result = exisingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);

            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }
}
