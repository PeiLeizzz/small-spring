package com.peilei.springframework.beans.definition;

/**
 * 描述 Bean 的属性
 */
public class PropertyValue {
    /**
     * 字段名称
     */
    private final String name;
    /**
     * 字段值，基础类型 / BeanReference
     */
    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
