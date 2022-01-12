package com.demkom58.springram.controller.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
@Component
public @interface BotController {
    String[] value() default {};
}
