package com.peilei.springframework.util;

public class ClassUtils {
    /**
     * 获取默认的类加载器
     * @return
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 不能获取线程上下文中的类加载器
            // 采用系统类加载器
        }

        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }
}
