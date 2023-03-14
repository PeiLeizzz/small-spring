package com.peilei.springframework.beans.processor;

import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.definition.PropertyValue;
import com.peilei.springframework.beans.definition.PropertyValues;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.peilei.springframework.core.io.DefaultResourceLoader;
import com.peilei.springframework.core.io.Resource;
import com.peilei.springframework.util.StringValueResolver;

import java.io.IOException;
import java.util.Properties;

/**
 * 占位符配置处理器
 */
public class PropertyPlaceholderProcessor implements BeanFactoryPostProcessor {
    // @value 注解的 prefix
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    // @value 注解的 suffix
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    // properties 配置文件的位置
    private String location;

    /**
     * 从 location 中读取配置信息检查所有的 BeanDefinition 并注入属性
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            // 加载属性的配置文件
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            // 占位符替换属性值、设置属性值
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    Object value = propertyValue.getValue();
                    if (!(value instanceof String)) {
                        continue;
                    }
                    String newValue = resolvePlaceholder((String) value, properties);
                    if (!newValue.equals(value)) {
                        propertyValues.setPropertyValue(propertyValue.getName(), newValue);
                    }
                }
            }

            // 向容器中添加字符串解析器，供解析 @Value 注解使用
            StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(properties);
            beanFactory.addEmbeddedValueResolver(valueResolver);
        } catch (IOException e) {
            throw new BeansException("Could not load properties: ", e);
        }
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 解析 ${} 中的 key 在配置文件中对应的 value
     * @param value
     * @param properties
     * @return
     */
    private String resolvePlaceholder(String value, Properties properties) {
        int startIdx = value.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
        int stopIdx = value.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);
        if (startIdx != -1 && stopIdx != -1 && startIdx < stopIdx) {
            // ${} 中定义的 key
            String propKey = value.substring(startIdx + 2, stopIdx);
            // properties 配置文件中定义的 value
            return properties.getProperty(propKey);
        }
        return value;
    }

    /**
     * ${} 的字符串处理类
     */
    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {
        private final Properties properties;

        public PlaceholderResolvingStringValueResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String resolveStringValue(String strVal) {
            return PropertyPlaceholderProcessor.this.resolvePlaceholder(strVal, properties);
        }
    }
}