package com.demkom58.springram.controller.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation that marks method as command exception handler.
 * You can use it in classes that marked with
 * {@link BotController BotController} annotation.
 *
 * @author Max Demydenko
 * @since 0.5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {
    /**
     * Alias for {@link ExceptionHandler#exception()}.
     */
    @AliasFor("exception")
    Class<? extends Throwable>[] value() default {};

    /**
     * Specifies exceptions classes that can be handled
     * by annotated method handler.
     */
    @AliasFor("value")
    Class<? extends Throwable>[] exception() default {};
}
