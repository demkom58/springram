package com.demkom58.springram.controller;

import com.demkom58.springram.controller.config.PathMatchingConfigurer;
import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.message.TelegramMessage;
import com.demkom58.springram.controller.method.HandlerMapping;
import com.demkom58.springram.controller.method.TelegramMessageHandler;
import com.demkom58.springram.controller.method.TelegramMessageHandlerMethod;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandlerComposite;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.Method;
import java.util.*;

@Component
public class CommandContainer {
    private static final Logger log = LoggerFactory.getLogger(CommandContainer.class);

    private final Map<MessageType, Map<String, TelegramMessageHandler>> directMap =
            Maps.newEnumMap(new HashMap<MessageType, Map<String, TelegramMessageHandler>>() {{
                for (MessageType value : MessageType.pathMethods())
                    put(value, Maps.newHashMap());
            }});
    private final Map<MessageType, Map<String, TelegramMessageHandler>> patternMap =
            Maps.newEnumMap(new HashMap<MessageType, Map<String, TelegramMessageHandler>>() {{
                for (MessageType value : MessageType.pathMethods())
                    put(value, Maps.newHashMap());
            }});

    private PathMatchingConfigurer pathMatchingConfigurer = new PathMatchingConfigurer();

    private HandlerMethodReturnValueHandlerComposite returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
    private HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();

    public PathMatchingConfigurer getPathMatchingConfigurer() {
        return pathMatchingConfigurer;
    }

    public void setPathMatchingConfigurer(PathMatchingConfigurer pathMatchingConfigurer) {
        this.pathMatchingConfigurer = pathMatchingConfigurer;
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

    public void addBotController(String path, TelegramMessageHandlerMethod controller) {
        final HandlerMapping mapping = controller.getMapping();
        final Method mtd = controller.getMethod();
        final MessageType[] eventTypes = mapping.messageTypes();

        final PathMatcher pathMatcher = getPathMatcher();
        for (MessageType messageType : eventTypes) {
            final boolean canHasPath = messageType.canHasPath();
            if (canHasPath) {
                log.trace("Adding method handler for message type {} with path: {}", messageType, path);
                if (pathMatcher.isPattern(path)) {
                    final var prev = patternMap.get(messageType).putIfAbsent(path, controller);
                    if (prev != null) {
                        throw new IllegalStateException(
                                "Cant register handler with pattern mapping '" + path
                                        + "' for method '" + mtd.getName() + "'"
                        );
                    }
                } else {
                    final var prev = directMap.get(messageType).putIfAbsent(path, controller);
                    if (prev != null) {
                        throw new IllegalStateException(
                                "Cant register handler with direct mapping '" + path
                                        + "' for method '" + mtd.getName() + "'"
                        );
                    }
                }
            }
        }
    }

    public void handle(Update update, AbsSender bot) throws Exception {
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

        TelegramMessageHandler handler = findControllers(eventType, commandText);
        if (handler == null) {
            return;
        }

        final Map<String, String> variables = getPathMatcher()
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

    @Nullable
    private TelegramMessageHandler findControllers(MessageType method, String command) {
        var directHandler = directMap.get(method).get(command.toLowerCase());
        if (directHandler != null) {
            return directHandler;
        }

        final List<TelegramMessageHandler> handlers = new ArrayList<>();
        final var entries = patternMap.get(method).entrySet();

        final PathMatcher pathMatcher = getPathMatcher();
        for (Map.Entry<String, TelegramMessageHandler> entry : entries) {
            final String key = entry.getKey();
            if (pathMatcher.match(key, command)) {
                handlers.add(entry.getValue());
            }
        }

        if (handlers.isEmpty()) {
            return null;
        }

        final Comparator<String> patternComparator = pathMatcher.getPatternComparator(command);
        handlers.sort((c1, c2) -> patternComparator.compare(
                c1.getMapping().value(),
                c2.getMapping().value()
        ));

        return handlers.get(0);
    }

    public PathMatcher getPathMatcher() {
        return pathMatchingConfigurer.getPathMatcher();
    }

}
