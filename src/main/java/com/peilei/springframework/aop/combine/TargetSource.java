package com.peilei.springframework.aop.combine;

import com.peilei.springframework.util.ClassUtils;

/**
 * 包装被代理的目标对象
 */
public class TargetSource {
    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    /**
     * 获取目标对象的接口
     * 将代理对象的创建移至 Bean 初始化之后
     * 那么 Bean 可能是通过 JDK / Cglib 初始化的
     * 所以增加需要判断
     * @return
     */
    public Class<?>[] getTargetClass() {
        Class<?> clazz = this.target.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
        return clazz.getInterfaces();
    }

    /**
     * 获取目标对象
     * @return
     */
    public Object getTarget() {
        return this.target;
    }
}
