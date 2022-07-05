package com.demkom58.springram.security;

import com.demkom58.springram.controller.message.SpringramMessage;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public record UserActionContext(User user, @Nullable Chat chat, SpringramMessage message, AbsSender bot) {
}
