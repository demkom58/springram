package com.demkom58.springram.controller.config;

import com.demkom58.springram.controller.container.CommandHandlerContainer;
import com.demkom58.springram.controller.TelegramCommandDispatcher;
import com.demkom58.springram.controller.UpdateBeanPostProcessor;
import com.demkom58.springram.controller.container.ExceptionHandlerContainer;
import com.demkom58.springram.controller.message.SpringramMessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Configuration
@Import({
        TelegramCommandDispatcher.class,
        CommandHandlerContainer.class,
        ExceptionHandlerContainer.class,
        SpringramMessageFactory.class
})
public class SpringramAutoConfiguration {
    private final SpringramConfigurerComposite configurerComposite = new SpringramConfigurerComposite();

    @Bean
    public UpdateBeanPostProcessor updateBeanPostProcessor(
            TelegramCommandDispatcher commandDispatcher,
            CommandHandlerContainer commandContainer,
            ExceptionHandlerContainer exceptionContainer) {
        return new UpdateBeanPostProcessor(commandDispatcher, commandContainer, exceptionContainer, configurerComposite);
    }

    @Autowired(required = false)
    public void addMvcConfigurers(List<SpringramConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
            this.configurerComposite.addAll(configurers);
        }
    }
}
