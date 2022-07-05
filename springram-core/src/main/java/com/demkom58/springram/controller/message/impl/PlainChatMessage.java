package com.demkom58.springram.controller.message.impl;

import com.demkom58.springram.controller.message.ChatMessage;
import com.demkom58.springram.controller.message.MessageType;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class PlainChatMessage extends PlainMessage implements ChatMessage {
    protected final Chat chat;

    public PlainChatMessage(Update update, MessageType eventType, User fromUser, Chat chat) {
        super(update, eventType, fromUser);
        this.chat = chat;
    }

    @Override
    public Long getChatId() {
        return chat.getId();
    }

    @Override
    public Chat getChat() {
        return chat;
    }

    @Override
    public String toString() {
        return "PlainChatMessage{" +
                "chat=" + chat +
                ", update=" + update +
                ", eventType=" + eventType +
                ", fromUser=" + fromUser +
                ", attributes=" + attributes +
                '}';
    }
}
