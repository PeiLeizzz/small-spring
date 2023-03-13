package com.peilei.springframework.context.annotation;

import java.lang.annotation.*;

/**
 * 用于配置作用域的注解，默认为 singletion
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    String value() default "singleton";
}
