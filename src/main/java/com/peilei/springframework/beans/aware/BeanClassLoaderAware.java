package com.peilei.springframework.beans.aware;

/**
 * Bean 的类加载器感知接口，即能感知到所属的类加载器
 */
public interface BeanClassLoaderAware extends Aware {
    void setBeanClassLoader(ClassLoader classLoader);
}
