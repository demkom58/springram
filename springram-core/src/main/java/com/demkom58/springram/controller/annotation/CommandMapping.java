package com.demkom58.springram.controller.annotation;

import com.demkom58.springram.controller.message.MessageType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation that marks method as command handler.
 * You can use it in classes that marked with
 * {@link BotController BotController} annotation.
 *
 * @author Max Demydenko
 * @since 0.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandMapping {
    /**
     * Alias for {@link CommandMapping#path()}.
     */
    @AliasFor("path")
    String[] value() default {};

    /**
     * Describes path of the command.
     */
    @AliasFor("value")
    String[] path() default {};

    /**
     * Events that will be listened, by default is {@link MessageType#TEXT_MESSAGE}
     */
    MessageType[] event() default {};
}
