package com.demkom58.springram.util;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public final class TestingUtil {
    public static Update createTextMessage(String messageText) {
        var user = new User(10L, "name", true, "last", "username",
                "en", true, true, true);

        var message = new Message();
        message.setText(messageText);
        message.setMessageId(645465);
        message.setChat(new Chat(100L, "Chat"));
        message.setFrom(user);

        final Update update = new Update();
        update.setMessage(message);
        update.setUpdateId(1021030);

        return update;
    }
}
