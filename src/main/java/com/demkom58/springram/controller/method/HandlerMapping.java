package com.demkom58.springram.controller.method;

import com.demkom58.springram.controller.message.MessageType;

public record HandlerMapping(MessageType[] messageTypes, String value) {
}
