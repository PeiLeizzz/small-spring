package com.peilei.springframework.beans.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean 属性的集合
 */
public class PropertyValues {
    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    public void addPropertyValue(PropertyValue pv) {
        this.propertyValueList.add(pv);
    }

    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }

    /**
     * 根据属性名获取属性值
     * @param propertyName
     * @return
     */
    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    /**
     * 设置属性值（用于更换 ${}）中的值
     * @param propertyName
     * @param propertyValue
     */
    public void setPropertyValue(String propertyName, Object propertyValue) {
        for (PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                pv.setValue(propertyValue);
                return;
            }
        }
    }
}
