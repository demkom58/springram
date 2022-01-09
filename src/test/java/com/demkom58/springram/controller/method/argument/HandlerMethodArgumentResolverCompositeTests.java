package com.demkom58.springram.controller.method.argument;

import com.demkom58.springram.TelegramLongPollingMvcBot;
import com.demkom58.springram.controller.CommandContainer;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.message.TelegramMessage;
import com.demkom58.springram.controller.method.argument.impl.PathVariablesHandlerMethodArgumentResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class HandlerMethodArgumentResolverCompositeTests {
    final Method handlerMethod = TestController.class.getDeclaredMethod("test", String.class);
    final MethodParameter parameter = new MethodParameter(handlerMethod, 0);

    HandlerMethodArgumentResolverCompositeTests() throws NoSuchMethodException {}

    @Test
    void addAndIsSupported() {
        final var resolvers = new HandlerMethodArgumentResolverComposite();

        // check for emptiness of new composite
        assertFalse(resolvers.isSupported(parameter));

        // check for existence via isSupported
        final var mockedRes = mock(HandlerMethodArgumentResolver.class);
        doReturn(true).when(mockedRes).isSupported(parameter);

        resolvers.add(mockedRes);

        assertTrue(resolvers.isSupported(parameter));
        verify(mockedRes).isSupported(parameter);
    }

    @Test
    void resolveTest() throws Exception {
        final var resolvers = new HandlerMethodArgumentResolverComposite();

        final TelegramMessage message = mock(TelegramMessage.class);
        final AbsSender bot = mock(AbsSender.class);

        final var mockedRes = mock(HandlerMethodArgumentResolver.class);

        doReturn(true).when(mockedRes).isSupported(parameter);
        doReturn(new SendMessage()).when(mockedRes).resolve(parameter, message, bot);

        resolvers.add(mockedRes);
        resolvers.resolve(parameter, message, bot);
        verify(mockedRes).resolve(parameter, message, bot);
    }

    static class TestController {
        @CommandMapping("test {test}")
        SendMessage test(@PathVariable String test) {
            return SendMessage.builder().text(test).build();
        }
    }
}
