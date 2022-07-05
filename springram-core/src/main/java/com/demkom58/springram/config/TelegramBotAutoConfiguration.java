package com.demkom58.springram.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Import(TelegramBotApiConfig.class)
public class TelegramBotAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(TelegramBotAutoConfiguration.class);
    private final List<BotSession> sessions = new ArrayList<>();
    private final List<LongPollingBot> longPollingBots;
    private final TelegramBotsApi botsApi;

    @Autowired
    public TelegramBotAutoConfiguration(List<LongPollingBot> longPollingBots, TelegramBotsApi botsApi) {
        this.longPollingBots = longPollingBots;
        this.botsApi = botsApi;
    }

    @PostConstruct
    public void start() {
        log.debug("Starting register bots");
        longPollingBots.forEach(bot -> {
            try {
                sessions.add(botsApi.registerBot(bot));
                log.info("Bot '{}' has been registered!", bot.getClass().getName());
            } catch (TelegramApiException e) {
                log.error("An error occurred while registering bot.", e);
            }
        });
        log.debug("Bots registration done");
    }

    @PreDestroy
    public void stop() {
        sessions.forEach(BotSession::stop);
    }
}
