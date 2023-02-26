package com.peilei.springframework.test.common;

import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.processor.BeanPostProcessor;
import com.peilei.springframework.test.bean.UserService;

public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("userService")) {
            UserService userService = (UserService) bean;
            userService.setLocation("北京");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
