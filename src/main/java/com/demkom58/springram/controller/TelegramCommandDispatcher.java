package com.demkom58.springram.controller;

import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.message.TelegramMessage;
import com.demkom58.springram.controller.method.TelegramMessageHandler;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandlerComposite;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Objects;

@Component
public class TelegramCommandDispatcher {
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
    private HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();
    private final CommandContainer commandContainer;

    public TelegramCommandDispatcher(CommandContainer commandContainer) {
        this.commandContainer = commandContainer;
    }

    public void dispatch(Update update, AbsSender bot) throws Exception {
        Objects.requireNonNull(update, "Update can't be null!");
        Objects.requireNonNull(bot, "Receiver bot can't be null!");

        final TelegramMessage message = TelegramMessage.from(update);
        if (message == null) {
            return;
        }

        final MessageType eventType = message.getEventType();
        final String messageText = message.getText();
        final String commandText = toCommand(bot, messageText);
        if (commandText == null) {
            return;
        }

        TelegramMessageHandler handler = commandContainer.findHandler(eventType, commandText);
        if (handler == null) {
            return;
        }

        final Map<String, String> variables = commandContainer.getPathMatchingConfigurer().getPathMatcher()
                .extractUriTemplateVariables(handler.getMapping().value(), commandText);

        message.setAttribute("variables", variables);
        final Object result = handler.invoke(argumentResolvers, message, bot, message, bot);
        if (result == null) {
            return;
        }

        final MethodParameter returnType = handler.getReturnType();
        final boolean supported = returnValueHandlers.isSupported(returnType);
        if (supported) {
            returnValueHandlers.handle(returnType, message, bot, result);
        } else {
            throw new UnsupportedOperationException("Unsupported return type '" +
                    returnType.getParameterType().getName() + "' in method '" + returnType.getMethod() + "'");
        }
    }

    @Nullable
    private String toCommand(AbsSender bot, String message) throws TelegramApiException {
        if (!StringUtils.hasText(message)) {
            return null;
        }

        if (commandContainer.getPathMatchingConfigurer().isCommandSlashMatch() && message.startsWith("/")) {
            message = message.substring(1);
        }

        final String[] line = message.split(" ", 2);
        final String[] commandParts = line[0].split("@", 2);
        if (commandParts.length == 2) {
            final String botUserName = bot.getMe().getUserName();
            final boolean forMe = commandParts[1].equalsIgnoreCase(botUserName);
            if (!forMe) {
                return null;
            }

            return commandParts[0] + " " + (line.length == 2 ? line[1] : "");
        } else {
            return message;
        }
    }

    public HandlerMethodReturnValueHandlerComposite getReturnValueHandlers() {
        return returnValueHandlers;
    }

    public void setReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }

    public HandlerMethodArgumentResolverComposite getArgumentResolvers() {
        return argumentResolvers;
    }

    public void setArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }
}
