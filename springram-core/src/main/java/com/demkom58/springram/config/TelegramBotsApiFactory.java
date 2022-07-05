package com.demkom58.springram.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook;

@Component
public class TelegramBotsApiFactory {
    private final TelegramProperties properties;

    @Autowired
    public TelegramBotsApiFactory(TelegramProperties properties) {
        this.properties = properties;
    }

    public TelegramBotsApi create() throws TelegramApiException {
        if (properties.hasInternalUrl()) {
            DefaultWebhook webhook = new DefaultWebhook();
            webhook.setInternalUrl(properties.getInternalUrl());

            if (properties.hasKeyStore()) {
                webhook.setKeyStore(properties.getKeyStore(), properties.getKeyStorePassword());
            }

            return new TelegramBotsApi(DefaultBotSession.class, webhook);
        }

        return new TelegramBotsApi(DefaultBotSession.class);
    }
}
