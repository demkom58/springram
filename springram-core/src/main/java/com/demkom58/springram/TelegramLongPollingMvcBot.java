package com.demkom58.springram;

import com.demkom58.springram.controller.TelegramCommandDispatcher;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Default implementation of {@link TelegramLongPollingBot TelegramLongPollingBot}
 * for Springram that pass updates to {@link TelegramCommandDispatcher CommandDispatcher}
 *
 * @author Max Demydenko
 * @since 0.1
 */
public class TelegramLongPollingMvcBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final TelegramCommandDispatcher commandDispatcher;

    public TelegramLongPollingMvcBot(String botUsername, String botToken, TelegramCommandDispatcher commandDispatcher) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            commandDispatcher.dispatch(update, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
