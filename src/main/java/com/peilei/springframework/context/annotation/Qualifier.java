package com.peilei.springframework.context.annotation;

import java.lang.annotation.*;

/**
 * 指定 Bean 名称注入对象注解
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Qualifier {
    String value() default "";
}
