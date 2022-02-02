package com.demkom58.springram.controller.method;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.message.SpringramMessage;
import com.demkom58.springram.controller.message.SpringramMessageFactory;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.springram.util.TestingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class TelegramMessageHandlerMethodTests {
    private TelegramMessageHandlerMethod handlerMethod;
    private HandlerMethodArgumentResolverComposite resolverComposite;
    private AbsSender bot;
    private SpringramMessageFactory messageFactory;

    @BotController
    static class TestController {
        @CommandMapping("test {test}")
        SendMessage test(@PathVariable String test) {
            return new SendMessage("23423", "1234");
        }
    }

    @BeforeEach
    void init() throws Exception {
        TestController controller = new TestController();

        handlerMethod = new TelegramMessageHandlerMethod(
                new HandlerMapping(new MessageType[]{MessageType.TEXT_MESSAGE}, "test {test}"),
                controller,
                controller.getClass().getDeclaredMethod("test", String.class));

        resolverComposite = mock(HandlerMethodArgumentResolverComposite.class);
        bot = mock(AbsSender.class);
        messageFactory = new SpringramMessageFactory();
    }

    @Test
    void invoke_valid_success() throws Exception {
        final Update update = TestingUtil.createTextMessage("test test");
        final SpringramMessage message = Objects.requireNonNull(messageFactory.create(update));

        doReturn(true).when(resolverComposite).isSupported(any());
        doReturn("test").when(resolverComposite).resolve(any(), same(message), same(bot));

        final Object result = handlerMethod.invoke(resolverComposite, message, bot, bot, message);
        assertNotNull(result);

        verify(resolverComposite).isSupported(any());
        verify(resolverComposite).resolve(any(), same(message), same(bot));
    }

    @Test
    void invoke_noSupportedResolver_exception() throws Exception {
        final Update update = TestingUtil.createTextMessage("test test");
        final SpringramMessage message = Objects.requireNonNull(messageFactory.create(update));

        doReturn(false).when(resolverComposite).isSupported(any());

        assertThrows(IllegalStateException.class,
                () -> handlerMethod.invoke(resolverComposite, message, bot, bot, message)
        );

        verify(resolverComposite).isSupported(any());
    }


}
