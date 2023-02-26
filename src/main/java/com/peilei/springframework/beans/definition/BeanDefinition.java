package com.peilei.springframework.beans.definition;

/**
 * 用于存放 Bean 对象定义
 */
public class BeanDefinition {
    /**
     * 该 Bean 对应的 Class 对象
     * 可以根据它获得相应的方法和字段信息
     */
    private Class beanClass;

    /**
     * 该 Bean 包含的属性值集合
     */
    private PropertyValues propertyValues;

    /**
     * Bean 初始化方法
     */
    private String initMethodName;

    /**
     * Bean 销毁方法
     */
    private String destroyMethodName;

    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
        this.propertyValues = new PropertyValues();
    }

    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        // 保证 propertyValues 非空
        this.propertyValues = propertyValues != null? propertyValues: new PropertyValues();
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }
}
