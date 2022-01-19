package com.demkom58.springram.controller;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.container.CommandContainer;
import com.demkom58.springram.controller.message.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
class CommandContainerTests {
    private CommandContainer container;

    @BotController
    static class TestController {
        @CommandMapping("test {test}")
        SendMessage test(@PathVariable String test) {
            return new SendMessage("23423", "1234");
        }
    }

    @BotController
    static class PatternFirstTestController {
        @CommandMapping("{test} test")
        SendMessage test(@PathVariable String test) {
            return new SendMessage("23423", "1234");
        }
    }

    @BeforeEach
    void init() {
        container = new CommandContainer();
    }

    @Test
    void addMethod_testController_success() throws Exception {
        final TestController testController = new TestController();
        container.addMethod(testController, TestController.class.getDeclaredMethod("test", String.class));
    }

    @Test
    void addMethod_doubleRegisterTestController_exception() throws Exception {
        final TestController testController = new TestController();
        container.addMethod(testController, TestController.class.getDeclaredMethod("test", String.class));
        assertThrows(
                IllegalStateException.class,
                () -> container.addMethod(testController,
                        TestController.class.getDeclaredMethod("test", String.class)
                )
        );
    }

    @Test
    void addMethod_patternFirstTestController_exception() throws Exception {
        assertThrows(
                IllegalArgumentException.class,
                () -> container.addMethod(new PatternFirstTestController(),
                        PatternFirstTestController.class.getDeclaredMethod("test", String.class)
                )
        );
    }

    @Test
    void findControllers_getRegisteredMethod_success() throws Exception {
        final TestController testController = new TestController();
        container.addMethod(testController, TestController.class.getDeclaredMethod("test", String.class));
        assertNotNull(container.findHandler(MessageType.TEXT_MESSAGE, "default", "test test"));
    }

    @Test
    void findControllers_getNotRegisteredMethod_null() throws Exception {
        assertNull(container.findHandler(MessageType.TEXT_MESSAGE, "default", "test test"));
    }

}
