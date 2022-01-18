package com.demkom58.springram.controller.method.argument;

import com.demkom58.springram.controller.config.SpringramConfigurer;
import com.demkom58.springram.controller.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

/**
 * Resolver interface for providing arguments to handler
 * methods. For creating custom resolver implement it,
 * then specify it in your
 * {@link SpringramConfigurer#configureArgumentResolvers(List)}
 *
 * @author Max Demydenko
 * @since 0.1
 */
public interface HandlerMethodArgumentResolver {
    boolean isSupported(MethodParameter parameter);

    @Nullable
    Object resolve(MethodParameter parameter, TelegramMessage message, AbsSender bot) throws Exception;
}
