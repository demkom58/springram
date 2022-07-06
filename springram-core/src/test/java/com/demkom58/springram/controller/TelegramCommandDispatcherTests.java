package com.demkom58.springram.controller;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.container.CommandHandlerContainer;
import com.demkom58.springram.controller.container.ExceptionHandlerContainer;
import com.demkom58.springram.controller.message.SpringramMessageFactory;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.springram.controller.method.argument.impl.PathVariablesHandlerMethodArgumentResolver;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandlerComposite;
import com.demkom58.springram.controller.method.result.impl.BotApiMethodHandlerMethodReturnValueHandler;
import com.demkom58.springram.util.TestingUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TelegramCommandDispatcherTests {
    private CommandHandlerContainer container;
    private ExceptionHandlerContainer exContainer;
    private TelegramCommandDispatcher dispatcher;
    private HandlerMethodReturnValueHandlerComposite handlers;
    private HandlerMethodArgumentResolverComposite resolvers;
    private Update update;

    @BotController
    static class TestController {
        @CommandMapping("test {test}")
        SendMessage test(@PathVariable String test) {
            return new SendMessage("23423", "1234");
        }
    }

    @BeforeAll
    void init() throws Exception {
        update = spy(TestingUtil.createTextMessage("test test"));

        container = spy(new CommandHandlerContainer());
        container.addMethod(new TestController(), TestController.class.getDeclaredMethod("test", String.class));
        exContainer = spy(new ExceptionHandlerContainer());

        dispatcher = new TelegramCommandDispatcher(container, exContainer, new SpringramMessageFactory(), new ArrayList<>());

        resolvers = spy(new HandlerMethodArgumentResolverComposite());
        resolvers.add(new PathVariablesHandlerMethodArgumentResolver());

        handlers = spy(new HandlerMethodReturnValueHandlerComposite());
        handlers.add(new BotApiMethodHandlerMethodReturnValueHandler());

        dispatcher.setArgumentResolvers(resolvers);
        dispatcher.setReturnValueHandlers(handlers);
    }

    @Test
    void dispatch_textMessage_success() throws Exception {
        final var bot = mock(AbsSender.class);
        dispatcher.dispatch(update, bot);

        verify(handlers).handle(any(), any(), same(bot), any());
        verify(resolvers).resolve(any(), any(), same(bot));
        verify(container).findHandler(any(), any(), any());
        verify(bot).execute(any(SendMessage.class));
    }

}
