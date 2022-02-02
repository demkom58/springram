package com.demkom58.springram.controller.message;

import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.bots.AbsSender;

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
    @Nullable
    private final Long chatId;
    @Nullable
    private final Chat chat;
    @Nullable
    private final String text;

    private final Map<String, Object> attributes = new HashMap<>();

    public TelegramMessage(Update update, MessageType eventType, User fromUser,
                           @Nullable Long chatId, @Nullable Chat chat, @Nullable String text) {
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

    @Nullable
    public Long getChatId() {
        return chatId;
    }

    @Nullable
    public Chat getChat() {
        return chat;
    }

    @Nullable
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
        Message message = null;

        User fromUser = null;
        Chat chat = null;
        String text = null;

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
        } else if (update.hasChatMember()) {
            eventType = MessageType.CHAT_MEMBER_STATUS_UPDATE;
            final ChatMemberUpdated chatMember = update.getChatMember();
            fromUser = chatMember.getFrom();
            chat = chatMember.getChat();
        } else if (update.hasChatJoinRequest()) {
            eventType = MessageType.CHAT_JOIN_REQUEST;
            final ChatJoinRequest chatJoinRequest = update.getChatJoinRequest();
            fromUser = chatJoinRequest.getUser();
            chat = chatJoinRequest.getChat();
        } else if (update.hasMyChatMember()) {
            eventType = MessageType.PERSONAL_CHAT_MEMBER_UPDATE;
            final ChatMemberUpdated myChatMember = update.getMyChatMember();
            fromUser = myChatMember.getFrom();
            chat = myChatMember.getChat();
        } else if (update.hasPollAnswer()) {
            eventType = MessageType.POLL_ANSWER;
            fromUser = update.getPollAnswer().getUser();
        } else if (update.hasCallbackQuery()) {
            eventType = MessageType.CALLBACK_QUERY;
            final CallbackQuery callbackQuery = update.getCallbackQuery();
            fromUser = callbackQuery.getFrom();
            chat = callbackQuery.getMessage().getChat();
        } else if (update.hasChosenInlineQuery()) {
            eventType = MessageType.INLINE_QUERY_CHOSEN;
            fromUser = update.getChosenInlineQuery().getFrom();
        } else if (update.hasInlineQuery()) {
            eventType = MessageType.INLINE_QUERY;
            fromUser = update.getInlineQuery().getFrom();
        } else if (update.hasPreCheckoutQuery()) {
            eventType = MessageType.PRE_CHECKOUT_QUERY;
            fromUser = update.getPreCheckoutQuery().getFrom();
        } else if (update.hasShippingQuery()) {
            eventType = MessageType.SHIPPING_QUERY;
            fromUser = update.getShippingQuery().getFrom();
        } else {
            return null;
        }

        if (message != null) {
            fromUser = message.getFrom();
            chat = message.getChat();
            text = message.getText();
        }

        if (fromUser == null) {
            return null;
        }

        return new TelegramMessage(
                update,
                eventType,
                fromUser,
                chat != null ? chat.getId() : null,
                chat,
                text
        );
    }
}
