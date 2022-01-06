package com.demkom58.springram.controller.method.result;

import com.demkom58.springram.controller.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface HandlerMethodReturnValueHandler {
    boolean isSupported(MethodParameter returnType);

    void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result) throws Exception;
}
