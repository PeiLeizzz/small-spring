package com.peilei.springframework.context.annotation;

import cn.hutool.core.util.ClassUtil;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 处理对象扫描装配的类
 */
public class ClassPathScanningCandidateComponentProvider {
    /**
     * 根据包路径扫描带有 @Component 注解的类并加入 BeanDefinition 集合中
     * @param basePackage
     * @return
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
        for (Class<?> clazz : classes) {
            candidates.add(new BeanDefinition(clazz));
        }
        return candidates;
    }
}
