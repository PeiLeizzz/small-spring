package com.peilei.springframework.beans.aware;

/**
 * Bean 名称感知接口，即能感知到所属的 BeanName
 */
public interface BeanNameAware extends Aware {
    void setBeanName(String beanName);
}
