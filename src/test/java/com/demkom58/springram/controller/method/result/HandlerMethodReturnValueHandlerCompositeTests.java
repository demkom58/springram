package com.demkom58.springram.controller.method.result;

import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.message.TelegramMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class HandlerMethodReturnValueHandlerCompositeTests {
    final Method handlerMethod = TestController.class.getDeclaredMethod("test", String.class);
    final MethodParameter parameter = new MethodParameter(handlerMethod, 0);

    HandlerMethodReturnValueHandlerCompositeTests() throws NoSuchMethodException {
    }

    @Test
    void isSupported_valid_success() {
        final var handlers = new HandlerMethodReturnValueHandlerComposite();

        // check for emptiness of new composite
        assertFalse(handlers.isSupported(parameter));

        // check for existence via isSupported
        final var mockedRes = mock(HandlerMethodReturnValueHandler.class);
        doReturn(true).when(mockedRes).isSupported(parameter);

        handlers.add(mockedRes);

        assertTrue(handlers.isSupported(parameter));
        verify(mockedRes).isSupported(parameter);
    }

    @Test
    void handle_valid_success() throws Exception {
        final var handlers = new HandlerMethodReturnValueHandlerComposite();

        final TelegramMessage message = mock(TelegramMessage.class);
        final SendMessage result = mock(SendMessage.class);
        final AbsSender bot = mock(AbsSender.class);

        final var mockedHandler = mock(HandlerMethodReturnValueHandler.class);
        doReturn(true).when(mockedHandler).isSupported(parameter);

        handlers.add(mockedHandler);
        handlers.handle(parameter, message, bot, result);

        verify(mockedHandler).handle(parameter, message, bot, result);
    }

    static class TestController {
        @CommandMapping("test {test}")
        SendMessage test(@PathVariable String test) {
            return SendMessage.builder().text(test).build();
        }
    }
}
