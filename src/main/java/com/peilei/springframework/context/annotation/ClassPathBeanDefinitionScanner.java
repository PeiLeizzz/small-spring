package com.peilei.springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.registry.BeanDefinitionRegistry;
import com.peilei.springframework.stereotype.Component;

import java.util.Set;

/**
 * 包路径扫描和处理作用域、BeanName 的类
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider{
    private BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * 扫描各个包路径，将作用域和 Bean 名称加入 BeanDefinition，然后注册到 BeanDefinitionRegistry 中
     * @param basePackages
     */
    public void doScan(String ...basePackages) {
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidates) {
                // 解析 Bean 的作用域
                String beanScope = resolveBeanScope(beanDefinition);
                if (StrUtil.isNotEmpty(beanScope)) {
                    beanDefinition.setScope(beanScope);
                }
                registry.registerBeanDefinition(determineBeanName(beanDefinition), beanDefinition);
            }
        }
    }

    /**
     * 获取 Bean 的作用域，如果 @Scope 注解中指定了，就使用指定的，否则为空
     * @param beanDefinition
     * @return
     */
    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (scope != null) {
            return scope.value();
        }
        return StrUtil.EMPTY;
    }

    /**
     * 获取 BeanName，如果 @Component 注解中指定了，就使用指定的，否则使用类名小写
     * @param beanDefinition
     * @return
     */
    private String determineBeanName(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Component component = beanClass.getAnnotation(Component.class);
        String value = component.value();
        if (StrUtil.isEmpty(value)) {
            // 默认采用类名首字母小写作为 BeanName
            value = StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return value;
    }
}
