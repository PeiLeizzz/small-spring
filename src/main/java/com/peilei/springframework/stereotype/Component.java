package com.peilei.springframework.stereotype;

import java.lang.annotation.*;

/**
 * 自动注入 Bean 的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
