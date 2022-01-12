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
    TEXT_MESSAGE(true),
    TEXT_MESSAGE_EDIT(true),
    TEXT_POST(true),
    TEXT_POST_EDIT(true),
    ;

    private static final MessageType[] pathMethods
            = Arrays.stream(values()).filter(MessageType::canHasPath).toArray(MessageType[]::new);
    private static final MessageType[] pathlessMethods
            = Arrays.stream(values()).filter(b -> !b.canHasPath).toArray(MessageType[]::new);

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
