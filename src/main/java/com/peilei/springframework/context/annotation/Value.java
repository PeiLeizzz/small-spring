package com.peilei.springframework.context.annotation;

import java.lang.annotation.*;

/**
 * 属性注入注解
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    String value();
}
