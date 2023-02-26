package com.peilei.springframework.beans.adapter;

import cn.hutool.core.util.StrUtil;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.processor.DisposableBean;

import java.lang.reflect.Method;

/**
 * 销毁方法的适配器
 * 同时支持接口和配置文件
 */
public class DisposableBeanAdapter implements DisposableBean {
    private final Object bean;
    private final String beanName;
    private String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = beanDefinition.getDestroyMethodName();
    }

    @Override
    public void destroy() throws Exception {
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
            return;
        }

        if (StrUtil.isNotEmpty(destroyMethodName)) {
            Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
            if (destroyMethod == null) {
                throw new BeansException("Couldn't find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
            }
            destroyMethod.invoke(bean);
        }
    }
}
