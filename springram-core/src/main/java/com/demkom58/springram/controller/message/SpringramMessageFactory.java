package com.demkom58.springram.controller.message;

import com.demkom58.springram.controller.message.impl.PlainChatMessage;
import com.demkom58.springram.controller.message.impl.PlainChatTextMessage;
import com.demkom58.springram.controller.message.impl.PlainMessage;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Objects;

@Component
public class SpringramMessageFactory {
    @Nullable
    public SpringramMessage create(Update update) {
        Objects.requireNonNull(update, "Update object can't be null!");

        if (update.hasMessage()) {
            return new PlainChatTextMessage(update, MessageType.TEXT_MESSAGE, update.getMessage());
        } else if (update.hasEditedMessage()) {
            return new PlainChatTextMessage(update, MessageType.TEXT_MESSAGE_EDIT, update.getEditedMessage());
        } else if (update.hasChannelPost()) {
            return new PlainChatTextMessage(update, MessageType.TEXT_POST, update.getChannelPost());
        } else if (update.hasEditedChannelPost()) {
            return new PlainChatTextMessage(update, MessageType.TEXT_POST_EDIT, update.getEditedChannelPost());
        } else if (update.hasChatMember()) {
            final ChatMemberUpdated upd = update.getChatMember();
            return new PlainChatMessage(update, MessageType.CHAT_MEMBER_STATUS_UPDATE, upd.getFrom(), upd.getChat());
        } else if (update.hasChatJoinRequest()) {
            final ChatJoinRequest req = update.getChatJoinRequest();
            return new PlainChatMessage(update, MessageType.CHAT_JOIN_REQUEST, req.getUser(), req.getChat());
        } else if (update.hasMyChatMember()) {
            final ChatMemberUpdated upd = update.getMyChatMember();
            return new PlainChatMessage(update, MessageType.PERSONAL_CHAT_MEMBER_UPDATE, upd.getFrom(), upd.getChat());
        } else if (update.hasPollAnswer()) {
            return new PlainMessage(update, MessageType.POLL_ANSWER, update.getPollAnswer().getUser());
        } else if (update.hasCallbackQuery()) {
            final CallbackQuery qu = update.getCallbackQuery();
            return new PlainChatMessage(update, MessageType.CALLBACK_QUERY, qu.getFrom(), qu.getMessage().getChat());
        } else if (update.hasChosenInlineQuery()) {
            return new PlainMessage(update, MessageType.INLINE_QUERY_CHOSEN, update.getChosenInlineQuery().getFrom());
        } else if (update.hasInlineQuery()) {
            return new PlainMessage(update, MessageType.INLINE_QUERY, update.getInlineQuery().getFrom());
        } else if (update.hasPreCheckoutQuery()) {
            return new PlainMessage(update, MessageType.PRE_CHECKOUT_QUERY, update.getPreCheckoutQuery().getFrom());
        } else if (update.hasShippingQuery()) {
            return new PlainMessage(update, MessageType.SHIPPING_QUERY, update.getShippingQuery().getFrom());
        }

        return null;
    }
}
