package com.demkom58.springram.controller.method.result;

import com.demkom58.springram.controller.config.SpringramConfigurer;
import com.demkom58.springram.controller.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

/**
 * Handler interface for handling results of handler
 * methods. For creating custom handler implement it,
 * then specify it in your
 * {@link SpringramConfigurer#configureReturnValueHandlers(List)}
 *
 * @author Max Demydenko
 * @since 0.1
 */
public interface HandlerMethodReturnValueHandler {
    boolean isSupported(MethodParameter returnType);

    void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result) throws Exception;
}
