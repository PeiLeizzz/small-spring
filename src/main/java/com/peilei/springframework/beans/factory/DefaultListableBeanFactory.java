package com.peilei.springframework.beans.factory;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.registry.BeanDefinitionRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean 工厂的默认实现类
 * 包括注册管理 BeanDefinition、构造 Bean 对象、添加和执行 BeanPostProcessor 处理、预先单例化 Bean 对象
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {
    /**
     * Bean 定义的注册表
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeansException("No bean named '" + beanName + "' is defined");
        }
        return beanDefinition;
    }

    /**
     * 对注册表中现有的对象进行预先单例化
     * @throws BeansException
     */
    @Override
    public void preInstantiateSingletons() throws BeansException {
        for (String beanName : beanDefinitionMap.keySet()) {
            getBean(beanName);
        }
    }

    /**
     * 根据 Class 对象构造并返回符合的 Bean 实例集合，包括其派生类
     * 主要便于构造 BeanFactoryPostProcessor 和 BeanPostProcessor 对象
     * @param type
     * @return
     * @param <T>
     * @throws BeansException
     */
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            Class beanClass = beanDefinition.getBeanClass();
            // type 的子类也要找出
            if (type.isAssignableFrom(beanClass)) {
                result.put(beanName, (T) getBean(beanName));
            }
        }
        return result;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    /**
     * 根据 Bean 类型获取 Bean 对象，如果有超过 1 个 Bean 对象都是这个类型，则报错
     * @param requiredType
     * @return
     * @param <T>
     * @throws BeansException
     */
    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        List<String> beanNames = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class beanClass = entry.getValue().getBeanClass();
            if (requiredType.isAssignableFrom(beanClass)) {
                beanNames.add(entry.getKey());
            }
        }
        if (beanNames.size() == 1) {
            return getBean(beanNames.get(0), requiredType);
        }
        throw new BeansException(requiredType + " expected single bean but found " + beanNames.size() + ": " + beanNames);
    }
}
