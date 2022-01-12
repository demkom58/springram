package com.demkom58.springram.controller.annotation;

import com.demkom58.springram.controller.message.MessageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks method as command handler.
 * You can use it in classes that marked with
 * {@link BotController BotController} annotation.
 *
 * @author Max Demydenko
 * @since 0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMapping {
    String[] value() default {""};

    MessageType[] event() default {MessageType.TEXT_MESSAGE};
}
