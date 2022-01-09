package com.demkom58.springram.controller.config;

import com.demkom58.springram.controller.CommandContainer;
import com.demkom58.springram.controller.TelegramCommandDispatcher;
import com.demkom58.springram.controller.UpdateBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Configuration
@Import({TelegramCommandDispatcher.class, CommandContainer.class})
public class TelegramMvcAutoConfiguration {
    private final TelegramMvcConfigurerComposite configurerComposite = new TelegramMvcConfigurerComposite();

    @Bean
    public UpdateBeanPostProcessor updateBeanPostProcessor(
            TelegramCommandDispatcher commandDispatcher,
            CommandContainer commandContainer) {
        return new UpdateBeanPostProcessor(commandDispatcher, commandContainer, configurerComposite);
    }

    @Autowired(required = false)
    public void addMvcConfigurers(List<TelegramMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurerComposite.addAll(configurers);
        }
    }
}
