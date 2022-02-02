package com.demkom58.springram.controller.message;

import org.telegram.telegrambots.meta.api.objects.Chat;

public interface ChatMessage extends SpringramMessage {
    Long getChatId();

    Chat getChat();
}
