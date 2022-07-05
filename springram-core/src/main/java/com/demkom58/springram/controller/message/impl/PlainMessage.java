package com.demkom58.springram.controller.message.impl;

import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.message.SpringramMessage;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlainMessage implements SpringramMessage {
    protected final Update update;
    protected final MessageType eventType;
    protected final User fromUser;

    protected final Map<String, Object> attributes = new HashMap<>();

    public PlainMessage(Update update, MessageType eventType, User fromUser) {
        this.update = update;
        this.eventType = eventType;
        this.fromUser = fromUser;
    }

    @Override
    public Update getUpdate() {
        return update;
    }

    @Override
    public MessageType getEventType() {
        return eventType;
    }

    @Override
    public User getFromUser() {
        return fromUser;
    }

    @Override
    public void setAttribute(String attributeName, Object value) {
        attributes.put(attributeName, value);
    }

    @Override
    @Nullable
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainMessage that = (PlainMessage) o;
        return update.equals(that.update)
                && attributes.equals(that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(update, attributes);
    }

    @Override
    public String toString() {
        return "TelegramMessage{" +
                "update=" + update +
                ", eventType=" + eventType +
                ", fromUser=" + fromUser +
                ", attributes=" + attributes +
                '}';
    }
}
