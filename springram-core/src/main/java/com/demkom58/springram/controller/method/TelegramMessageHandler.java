package com.demkom58.springram.controller.method;

import com.demkom58.springram.controller.message.SpringramMessage;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Interface to describe handler methods, like
 * controller's handler methods.
 *
 * @author Max Demydenko
 * @since 0.1
 */
public interface TelegramMessageHandler {
    @Nullable
    Object invoke(HandlerMethodArgumentResolverComposite resolvers,
                  SpringramMessage message,
                  AbsSender bot,
                  Object... providedArgs) throws Exception;

    HandlerMapping getMapping();

    MethodParameter getReturnType();
}
