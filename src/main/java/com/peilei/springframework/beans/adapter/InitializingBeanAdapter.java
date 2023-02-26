package com.peilei.springframework.beans.adapter;

import cn.hutool.core.util.StrUtil;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.processor.InitializingBean;

import java.lang.reflect.Method;

/**
 * 初始方法的适配器
 * 同时支持接口和配置文件
 */
public class InitializingBeanAdapter implements InitializingBean {
    private final Object bean;
    private final String beanName;
    private String initMethodName;

    public InitializingBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.initMethodName = beanDefinition.getInitMethodName();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
            return;
        }

        if (StrUtil.isNotEmpty(initMethodName)) {
            Method initMethod = bean.getClass().getMethod(initMethodName);
            if (initMethod == null) {
                throw new BeansException("Couldn't find a init method named '" + initMethod + "' on bean with name '" + beanName + "'");
            }
            initMethod.invoke(bean);
        }
    }
}
