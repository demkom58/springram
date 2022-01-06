package com.demkom58.springram;

import com.demkom58.springram.controller.CommandContainer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramLongPollingMvcBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final CommandContainer container;

    public TelegramLongPollingMvcBot(String botUsername, String botToken, CommandContainer container) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.container = container;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public CommandContainer getContainer() {
        return container;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            container.handle(update, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
