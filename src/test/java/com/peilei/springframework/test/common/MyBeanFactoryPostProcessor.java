package com.peilei.springframework.test.common;

import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.definition.PropertyValue;
import com.peilei.springframework.beans.definition.PropertyValues;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.peilei.springframework.beans.processor.BeanFactoryPostProcessor;

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("userService");
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("company", "字节跳动"));
    }
}
