package com.demkom58.springram.controller.config;

import com.demkom58.springram.controller.CommandContainer;
import com.demkom58.springram.controller.UpdateBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Configuration
public class TelegramMvcAutoConfiguration {
    private final TelegramMvcConfigurerComposite configurerComposite = new TelegramMvcConfigurerComposite();

    @Bean
    public CommandContainer commandContainer() {
        return new CommandContainer();
    }

    @Bean
    public UpdateBeanPostProcessor updateBeanPostProcessor(CommandContainer container) {
        return new UpdateBeanPostProcessor(container, configurerComposite);
    }

    @Autowired(required = false)
    public void addMvcConfigurers(List<TelegramMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurerComposite.addAll(configurers);
        }
    }
}
