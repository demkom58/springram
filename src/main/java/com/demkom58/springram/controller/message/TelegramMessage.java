package com.demkom58.springram.controller.message;

import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Wrapper for {@link Update update} with simplified
 * access to main attributes, such as text, author, chat.
 *
 * @author Max Demydenko
 * @since 0.1
 */
public class TelegramMessage {
    private final Update update;

    private final MessageType eventType;
    private final User fromUser;
    private final Long chatId;
    private final Chat chat;
    private final String text;

    private final Map<String, Object> attributes = new HashMap<>();

    public TelegramMessage(Update update, MessageType eventType, User fromUser, Long chatId, Chat chat, String text) {
        this.update = update;
        this.eventType = eventType;
        this.fromUser = fromUser;
        this.chatId = chatId;
        this.chat = chat;
        this.text = text;
    }

    public Update getUpdate() {
        return update;
    }

    public MessageType getEventType() {
        return eventType;
    }

    public User getFromUser() {
        return fromUser;
    }

    public Long getChatId() {
        return chatId;
    }

    public Chat getChat() {
        return chat;
    }

    public String getText() {
        return text;
    }

    public void setAttribute(String attributeName, Object value) {
        attributes.put(attributeName, value);
    }

    @Nullable
    public Object getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelegramMessage that = (TelegramMessage) o;
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
                ", chatId=" + chatId +
                ", chat=" + chat +
                ", text='" + text + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    @Nullable
    public static TelegramMessage from(Update update) {
        Objects.requireNonNull(update, "Update object can't be null!");

        final MessageType eventType;
        final Message message;

        if (update.hasMessage()) {
            eventType = MessageType.TEXT_MESSAGE;
            message = update.getMessage();
        } else if (update.hasEditedMessage()) {
            eventType = MessageType.TEXT_MESSAGE_EDIT;
            message = update.getEditedMessage();
        } else if (update.hasChannelPost()) {
            eventType = MessageType.TEXT_POST;
            message = update.getChannelPost();
        } else if (update.hasEditedChannelPost()) {
            eventType = MessageType.TEXT_POST_EDIT;
            message = update.getEditedChannelPost();
        } else {
            return null;
        }

        User fromUser = null;
        Long chatId = null;
        Chat chat = null;
        String text = null;

        if (message != null) {
            fromUser = message.getFrom();
            chatId = message.getChatId();
            chat = message.getChat();
            text = message.getText();
        }

        if (fromUser == null || chatId == null || text == null) {
            return null;
        }

        return new TelegramMessage(update, eventType, fromUser, chatId, chat, text);
    }
}
