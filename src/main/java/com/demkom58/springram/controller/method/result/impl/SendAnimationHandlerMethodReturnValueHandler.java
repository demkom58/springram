package com.demkom58.springram.controller.method.result.impl;

import com.demkom58.springram.controller.message.TelegramMessage;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class SendAnimationHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean isSupported(MethodParameter returnType) {
        return SendAnimation.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result)
            throws Exception {
        final SendAnimation sm = (SendAnimation) result;
        bot.execute(sm);
    }
}