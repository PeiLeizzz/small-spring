package com.peilei.springframework.beans.configurer;

import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.definition.PropertyValue;
import com.peilei.springframework.beans.definition.PropertyValues;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.peilei.springframework.beans.processor.BeanFactoryPostProcessor;
import com.peilei.springframework.core.io.DefaultResourceLoader;
import com.peilei.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * 占位符配置处理器
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {
    // @value 注解的 prefix
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    // @value 注解的 suffix
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    // properties 配置文件的位置
    private String location;

    /**
     * 从 location 中读取配置信息并注入到 BeanDefinition 中
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    Object value = propertyValue.getValue();
                    if (!(value instanceof String)) {
                        continue;
                    }

                    String strVal = (String) value;
                    int startIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
                    int stopIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);
                    if (startIdx != -1 && stopIdx != -1 && startIdx < stopIdx) {
                        // ${} 中定义的 key
                        String propKey = strVal.substring(startIdx + 2, stopIdx);
                        // properties 配置文件中定义的 value
                        String propVal = properties.getProperty(propKey);

                        // 添加在链表尾部，优先级更高，会覆盖同名的属性
                        propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), propVal));
                    }
                }
            }
        } catch (IOException e) {
            throw new BeansException("Could not load properties: ", e);
        }
    }

    public void setLocation(String location) {
        this.location = location;
    }
}