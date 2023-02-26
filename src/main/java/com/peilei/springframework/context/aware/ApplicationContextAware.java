package com.peilei.springframework.context.aware;

import com.peilei.springframework.beans.aware.Aware;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.context.ApplicationContext;

/**
 * 应用上下文感知接口，即能感知到所属的应用上下文
 */
public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
