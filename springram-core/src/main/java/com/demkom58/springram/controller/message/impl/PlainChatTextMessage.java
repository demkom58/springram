package com.demkom58.springram.controller.message.impl;

import com.demkom58.springram.controller.message.ChatTextMessage;
import com.demkom58.springram.controller.message.MessageType;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class PlainChatTextMessage extends PlainChatMessage implements ChatTextMessage {
    protected final String text;

    public PlainChatTextMessage(Update update, MessageType eventType, User fromUser, Chat chat, String text) {
        super(update, eventType, fromUser, chat);
        this.text = text;
    }

    public PlainChatTextMessage(Update update, MessageType eventType, Message message) {
        this(update, eventType, message.getFrom(), message.getChat(), message.getText());
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "PlainChatTextMessage{" +
                "chat=" + chat +
                ", text='" + text + '\'' +
                ", update=" + update +
                ", eventType=" + eventType +
                ", fromUser=" + fromUser +
                ", attributes=" + attributes +
                '}';
    }
}
