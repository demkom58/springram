package com.demkom58.springram.controller.method.result.impl;

import com.demkom58.springram.controller.message.TelegramMessage;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class SendStickerHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean isSupported(MethodParameter returnType) {
        return SendSticker.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result)
            throws Exception {
        final SendSticker sm = (SendSticker) result;
        bot.execute(sm);
    }
}