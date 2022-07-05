package com.demkom58.springram.controller.message;

import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public interface SpringramMessage {
    Update getUpdate();

    MessageType getEventType();

    User getFromUser();

    void setAttribute(String attributeName, Object value);

    @Nullable
    Object getAttribute(String attributeName);
}
