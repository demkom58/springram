package com.demkom58.springram.controller.method.result.impl;

import com.demkom58.springram.controller.message.TelegramMessage;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class AddStickerToSetHandlerMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean isSupported(MethodParameter returnType) {
        return AddStickerToSet.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handle(MethodParameter returnType, TelegramMessage message, AbsSender bot, Object result)
            throws Exception {
        final AddStickerToSet sm = (AddStickerToSet) result;
        bot.execute(sm);
    }
}