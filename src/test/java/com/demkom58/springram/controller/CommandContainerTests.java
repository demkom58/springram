package com.demkom58.springram.controller;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.container.CommandContainer;
import com.demkom58.springram.controller.container.PathAlreadyTakenException;
import com.demkom58.springram.controller.message.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
class CommandContainerTests {
    private static Method testControllerMethod;
    private static Method patternControllerMethod;

    static {
        try {
            testControllerMethod = TestController.class.getDeclaredMethod("test", String.class);
            patternControllerMethod = PatternFirstTestController.class.getDeclaredMethod("test", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

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
        container.addMethod(testController, testControllerMethod);
    }

    @Test
    void addMethod_doubleRegisterTestController_exception() throws Exception {
        final TestController testController = new TestController();
        container.addMethod(testController, testControllerMethod);
        assertThrows(
                PathAlreadyTakenException.class,
                () -> container.addMethod(testController, testControllerMethod)
        );
    }

    @Test
    void addMethod_patternFirstTestController_exception() throws Exception {
        assertThrows(
                IllegalArgumentException.class,
                () -> container.addMethod(new PatternFirstTestController(), patternControllerMethod)
        );
    }

    @Test
    void findControllers_getRegisteredMethod_success() throws Exception {
        final TestController testController = new TestController();
        container.addMethod(testController, testControllerMethod);
        assertNotNull(container.findHandler(MessageType.TEXT_MESSAGE, null, "test test"));
    }

    @Test
    void findControllers_getNotRegisteredMethod_null() throws Exception {
        assertNull(container.findHandler(MessageType.TEXT_MESSAGE, null, "test test"));
    }

}
