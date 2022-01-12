package com.demkom58.springram.controller.method;

import com.demkom58.springram.controller.message.MessageType;

/**
 * Contains information about a handler,
 * like message type and path.
 *
 * @author Max Demydenko
 * @since 0.1
 */
public record HandlerMapping(MessageType[] messageTypes, String value) {
}
