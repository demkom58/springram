package com.demkom58.springram.controller.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Marks class as bot controller and spring framework bean.
 *
 * You can define control methods like commands in class
 * marked with this annotation. To define command use
 * {@link CommandMapping CommandMapping} annotation on method.
 *
 * @author Max Demydenko
 * @since 0.1
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface BotController {
    /**
     * @return component name, if specified
     */
    @AliasFor(annotation = Component.class)
    String value() default "";
}
