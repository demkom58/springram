package com.demkom58.springram.controller;

import com.demkom58.springram.controller.container.CommandHandlerContainer;
import com.demkom58.springram.controller.container.ExceptionHandlerContainer;
import com.demkom58.springram.controller.message.*;
import com.demkom58.springram.controller.method.TelegramMessageHandler;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandlerComposite;
import com.demkom58.springram.controller.user.SpringramUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class TelegramCommandDispatcher {
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
    private HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();
    private final CommandHandlerContainer commandContainer;
    private final ExceptionHandlerContainer exceptionContainer;
    private final SpringramMessageFactory messageFactory;
    private final List<CommandPreHandler> preHandlers;

    public TelegramCommandDispatcher(CommandHandlerContainer commandContainer,
                                     ExceptionHandlerContainer exceptionContainer,
                                     SpringramMessageFactory messageFactory,
                                     List<CommandPreHandler> preHandlers) {
        this.commandContainer = commandContainer;
        this.exceptionContainer = exceptionContainer;
        this.messageFactory = messageFactory;
        this.preHandlers = preHandlers;
    }

    public void dispatch(Update update, AbsSender bot) throws Exception {
        Objects.requireNonNull(update, "Update can't be null!");
        Objects.requireNonNull(bot, "Receiver bot can't be null!");

        final SpringramMessage message = messageFactory.create(update);
        if (message == null) {
            return;
        }

        final MessageType eventType = message.getEventType();
        final String messageText = eventType.canHasPath() ? ((TextMessage) message).getText() : null;
        final String commandText = shortCommand(bot, messageText);

        final SpringramUserDetails userDetails = commandContainer.getPathMatchingConfigurer()
                .getUserDetailsService().loadById(message.getFromUser().getId());
        final String userChain = userDetails == null ? null : userDetails.getChain();

        TelegramMessageHandler handler = commandContainer.findHandler(eventType, userChain, commandText);
        if (handler == null) {
            return;
        }

        final String mapping = handler.getMapping().value();
        if (commandText != null && !ObjectUtils.isEmpty(mapping)) {
            final Map<String, String> variables = commandContainer.getPathMatchingConfigurer().getPathMatcher()
                    .extractUriTemplateVariables(mapping, commandText);
            message.setAttribute("variables", variables);
        }

        Object result = invokeHandler(bot, message, handler);
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
    private Object invokeHandler(AbsSender bot, SpringramMessage message, TelegramMessageHandler handler) throws Exception {
        Chat chat = null;
        if (message instanceof ChatMessage cm) {
            chat = cm.getChat();
        }

        final UserActionContext context = new UserActionContext(message.getFromUser(), chat, message, bot);
        for (CommandPreHandler preHandler : preHandlers) {
            preHandler.handle(context);
        }

        Object result;

        try {
            result = handler.invoke(argumentResolvers, message, bot, message, bot);
        } catch (Throwable throwable) {
            TelegramMessageHandler exHandler = exceptionContainer.findHandler(throwable.getClass(),
                    message.getEventType(),
                    handler.getMapping().chain(),
                    handler.getMapping().value()
            );
            if (exHandler != null) {
                result = exHandler.invoke(argumentResolvers, message, bot, message, bot, throwable);
            } else {
                throw throwable;
            }
        }

        return result;
    }


    @Nullable
    private String shortCommand(AbsSender bot, @Nullable String message) throws TelegramApiException {
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

            return line.length == 2 ? commandParts[0] + " " + line[1] : commandParts[0];
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
