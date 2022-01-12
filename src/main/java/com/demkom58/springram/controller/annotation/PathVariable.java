package com.demkom58.springram.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
public @interface PathVariable {
    String value() default "";
}
