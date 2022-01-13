package com.demkom58.springram.controller.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Marks argument as path variable, that specified
 * controller method path, annotated with
 * {@link CommandMapping CommandMapping}
 *
 * @author Max Demydenko
 * @since 0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface PathVariable {

    /**
     * Alias for name
     */
    @AliasFor("name")
    String value() default "";

    /**
     * Describes name of path variable
     */
    @AliasFor("value")
    String name() default "";
}
