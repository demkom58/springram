package com.demkom58.springram.controller.method.result.impl;

import com.demkom58.springram.controller.message.SpringramMessage;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class BotApiMethodHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean isSupported(MethodParameter returnType) {
        return BotApiMethod.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handle(MethodParameter returnType, SpringramMessage message, AbsSender bot, Object result)
            throws Exception {
        bot.execute((BotApiMethod<?>) result);
    }
}
