package com.demkom58.springram.controller.method.result.impl;

import com.demkom58.springram.controller.message.TelegramMessage;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class SendVideoNoteHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean isSupported(MethodParameter returnType) {
        return SendVideoNote.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result)
            throws Exception {
        final SendVideoNote sm = (SendVideoNote) result;
        bot.execute(sm);
    }
}