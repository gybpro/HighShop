package com.high.shop.log.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Log {

    /**
     * 动作
     * @return
     */
    @AliasFor("value")
    String operation() default "";

    /**
     * operation的别名
     * @return
     */
    @AliasFor("operation")
    String value() default "";

}
