package com.peilei.springframework.aop.combine;

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
     * @return
     */
    public Class<?>[] getTargetClass() {
        return this.target.getClass().getInterfaces();
    }

    /**
     * 获取目标对象
     * @return
     */
    public Object getTarget() {
        return this.target;
    }
}
