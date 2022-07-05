package com.demkom58.springram.controller.message;

import java.util.Arrays;

/**
 * Message type for determining the Telegram's
 * update type.
 *
 * @author Max Demydenko
 * @since 0.1
 */
public enum MessageType {
    /**
     * When chat text message received.
     *
     * Associated with {@link ChatTextMessage ChatTextMessage}.
     */
    TEXT_MESSAGE(true),
    /**
     * On chat text message edited.
     *
     * Associated with {@link ChatTextMessage ChatTextMessage}.
     */
    TEXT_MESSAGE_EDIT(true),
    /**
     * When received post in channel.
     *
     * Associated with {@link ChatTextMessage ChatTextMessage}.
     */
    TEXT_POST(true),
    /**
     * On channel post edited.
     *
     * Associated with {@link ChatTextMessage ChatTextMessage}.
     */
    TEXT_POST_EDIT(true),
    /**
     * A user status was updated in a chat.
     *
     * Associated with {@link ChatMessage ChatMessage}.
     *
     * @spec bot must be an administrator in the chat and must have
     * value "chat_member" in the list of allowed_updates to receive
     * these messages
     */
    CHAT_MEMBER_STATUS_UPDATE(false),
    /**
     * On new chat join request.
     *
     * Associated with {@link ChatMessage ChatMessage}.
     */
    CHAT_JOIN_REQUEST(false),
    /**
     * The user status was updated in a private chat.
     *
     * Associated with {@link ChatMessage ChatMessage}.
     *
     * @spec received when the bot is blocked or unblocked by the user
     */
    PERSONAL_CHAT_MEMBER_UPDATE(false),
    /**
     * Happens when message with poll received.
     *
     * Associated with {@link ChatMessage ChatMessage}.
     *
     * @spec bot receive polls, which are sent by the bot only
     */
    POLL_MESSAGE(false),
    /**
     * A user changed their answer in a non-anonymous poll.
     *
     * Associated with {@link SpringramMessage SpringramMessage}.
     *
     * @spec bot can receive new votes only in polls that were
     * sent by the bot itself
     */
    POLL_ANSWER(false),
    /**
     * On callback query received.
     *
     * Associated with {@link ChatMessage ChatMessage}.
     */
    CALLBACK_QUERY(false),
    /**
     * Result of choosing inline query by user.
     *
     * Associated with {@link SpringramMessage SpringramMessage}.
     */
    INLINE_QUERY_CHOSEN(false),
    /**
     * When inline query received.
     *
     * Associated with {@link SpringramMessage SpringramMessage}.
     */
    INLINE_QUERY(false),
    /**
     * On new pre-checkout query.
     *
     * Associated with {@link SpringramMessage SpringramMessage}.
     */
    PRE_CHECKOUT_QUERY(false),
    /**
     * On new shipping query.
     *
     * Associated with {@link SpringramMessage SpringramMessage}.
     *
     * @spec only for invoices with flexible price
     */
    SHIPPING_QUERY(false);

    private static final MessageType[] pathMethods = Arrays.stream(values()).filter(MessageType::canHasPath).toArray(MessageType[]::new);
    private static final MessageType[] pathlessMethods = Arrays.stream(values()).filter(b -> !b.canHasPath).toArray(MessageType[]::new);

    private final boolean canHasPath;

    MessageType(boolean canHasPath) {
        this.canHasPath = canHasPath;
    }

    public boolean canHasPath() {
        return canHasPath;
    }

    public static MessageType[] pathMethods() {
        return pathMethods;
    }

    public static MessageType[] pathlessMethods() {
        return pathlessMethods;
    }
}
