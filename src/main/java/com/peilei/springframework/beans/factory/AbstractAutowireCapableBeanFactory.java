package com.peilei.springframework.beans.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.TypeUtil;
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
import com.peilei.springframework.beans.processor.InstantiationAwareBeanPostProcessor;
import com.peilei.springframework.beans.strategy.CglibSubclassingInstantiationStrategy;
import com.peilei.springframework.beans.strategy.InstantiationStrategy;
import com.peilei.springframework.core.convert.ConversionService;

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
     * 创造 Bean 对象
     * @param beanName
     * @param beanDefinition
     * @param args
     * @return
     * @throws BeansException
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        // 判断是否返回代理对象
        Object bean = resolveBeforeInstantiation(beanName, beanDefinition);
        if (bean != null) {
            return bean;
        }

        return doCreateBean(beanName, beanDefinition, args);
    }

    /**
     * 实例化 Bean 对象并填充其属性的具体逻辑实现
     * factory bean process -- before instantiation -- create bean -- add to cache -- after instantiation
     * -- BeanPostProcessor apply property values -- xml apply property values
     * -- aware -- before process -- init method -- after process
     * -- register disposable bean -- add to singleton pool
     * @param beanName
     * @param beanDefinition
     * @param args
     * @return
     * @throws BeansException
     */
    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            // 通过构造函数实例化 Bean
            bean = createBeanInstance(beanDefinition, beanName, args);

            // 处理循环依赖，将实例化但还没有填充属性的对象提前放入缓存
            if (beanDefinition.isSingleton()) {
                Object finalBean = bean;
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
            }

            // 对象实例化后判断
            boolean continueWithPropertyPopulation = applyBeanPostProcessorsAfterInstantiation(beanName, bean);
            if (!continueWithPropertyPopulation) {
                return bean;
            }

            // 在设置 Bean 属性之前，允许 BeanPostProcessor 修改属性值
            applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);
            // 根据 Bean 定义对象中的属性值集合，填充 Bean 的属性值，会覆盖上面构造函数设置的值和 BeanPostProcessor 修改的值
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
            // 获取代理对象
            bean = getSingleton(beanName);
            registerSingleton(beanName, bean);
        }
        return bean;
    }

    /**
     * Bean 对象实例化前的行为，如果是代理对象，则不返回 null
     * @param beanName
     * @param beanDefinition
     * @return
     * @throws BeansException
     */
    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (bean != null) {
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
    }

    /**
     * 执行 Bean 对象实例化前的拦截器，如果是代理对象，则不返回 null
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                Object result = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessBeforeInstantiation(beanClass, beanName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
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
     *
     * @param beanName
     * @param beanDefinition
     * @param bean
     * @return
     */
    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) throws BeansException {
        Object exposedObject = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                exposedObject = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).getEarlyBeanReference(exposedObject, beanName);
                if (exposedObject == null) {
                    return null;
                }
            }
        }
        return exposedObject;
    }

    /**
     * Bean 实例化之后的操作，如果返回 false，则不再执行后续对 Bean 对象属性的操作
     * @param beanName
     * @param bean
     * @return
     */
    private boolean applyBeanPostProcessorsAfterInstantiation(String beanName, Object bean) throws BeansException {
        boolean continueWithPropertyPopulation = true;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor = (InstantiationAwareBeanPostProcessor) beanPostProcessor;
                if (!instantiationAwareBeanPostProcessor.postProcessAfterInstantiation(bean, beanName)) {
                    continueWithPropertyPopulation = false;
                    break;
                }
            }
        }
        return continueWithPropertyPopulation;
    }

    /**
     * 在设置 Bean 属性之前，允许 BeanPostProcessor#postProcessPropertyValues 修改属性值
     * @param beanName
     * @param bean
     * @param beanDefinition
     * @throws BeansException
     */
    protected void applyBeanPostProcessorsBeforeApplyingPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) throws BeansException {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                PropertyValues pvs = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).postProcessPropertyValues(beanDefinition.getPropertyValues(), bean, beanName);
                if (pvs != null) {
                    for (PropertyValue propertyValue : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                    }
                }
            }
        }
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

                if (value instanceof BeanReference) {
                    // 引用类型对象递归实例化与属性填充
                    BeanReference beanReference = (BeanReference) value;
                    // TODO：构造参数？
                    value = getBean(beanReference.getBeanName());
                } else {
                    // 类型转换
                    Class<?> sourceType = value.getClass();
                    Class<?> targetType = (Class<?>) TypeUtil.getFieldType(bean.getClass(), name);
                    ConversionService conversionService = getConversionService();
                    if (conversionService != null) {
                        if (conversionService.canConvert(sourceType, targetType)) {
                            value = conversionService.convert(value, targetType);
                        }
                    }
                }
                // 设置成员的值
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception e) {
            throw new BeansException("Error setting property values: " + beanName);
        }
    }

    /**
     * Bean 的初始化行为，aware - beforeProcess -- initMethod -- afterProcess
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
