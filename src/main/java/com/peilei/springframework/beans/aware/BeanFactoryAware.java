package com.peilei.springframework.beans.aware;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.BeanFactory;

/**
 * 容器感知接口，即能感知到所属的 BeanFactory
 */
public interface BeanFactoryAware extends Aware {
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
