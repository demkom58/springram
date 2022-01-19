package com.demkom58.springram.controller.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Makes handler methods visible only for users
 * with chain specified with this annotation.
 *
 * @author Max Demydenko
 * @since 0.3
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Chain {
    /**
     * Alias for {@link Chain#chain()}.
     */
    @AliasFor("chain")
    String[] value() default {};

    /**
     * Specifies names of chains via annotated
     * method will be accessible
     */
    @AliasFor("value")
    String[] chain() default {};
}
