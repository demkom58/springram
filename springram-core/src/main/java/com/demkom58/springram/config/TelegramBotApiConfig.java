package com.demkom58.springram.config;

import com.demkom58.springram.TelegramLongPollingMvcBot;
import com.demkom58.springram.controller.TelegramCommandDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
@Import({TelegramProperties.class, TelegramBotsApiFactory.class, TelegramCommandDispatcher.class})
public class TelegramBotApiConfig {
    private final TelegramBotsApiFactory apiFactory;

    public TelegramBotApiConfig(TelegramBotsApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return apiFactory.create();
    }

    @Bean
    @ConditionalOnMissingBean(TelegramLongPollingBot.class)
    public TelegramLongPollingBot defaultLongPollingBot(@Value("${bot.token}") String token,
                                                        @Value("${bot.username}") String username,
                                                        TelegramCommandDispatcher commandDispatcher) {
        return new TelegramLongPollingMvcBot(username, token, commandDispatcher);
    }
}
