package com.demkom58.springram.controller;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.config.PathMatchingConfigurer;
import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.method.HandlerMapping;
import com.demkom58.springram.controller.method.TelegramMessageHandler;
import com.demkom58.springram.controller.method.TelegramMessageHandlerMethod;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;

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

    public void addMethod(Object bean, Method method) {
        final BotController controller = bean.getClass().getAnnotation(BotController.class);
        final CommandMapping mapping = method.getAnnotation(CommandMapping.class);

        final Set<String> paths = new HashSet<>();

        final String[] controllerValues
                = ObjectUtils.isEmpty(controller.value()) ? new String[]{""} : controller.value();
        final String[] mappingValues
                = ObjectUtils.isEmpty(mapping.value()) ? new String[]{""} : mapping.value();

        final PathMatcher pathMatcher = pathMatchingConfigurer.getPathMatcher();
        for (String mappingValue : mappingValues) {
            final String[] cmd = mappingValue.split(" ", 2);
            final boolean isPattern = pathMatcher.isPattern(cmd[0]);
            if (isPattern) {
                throw new IllegalArgumentException(
                        "CommandMapping method with mappings (" + String.join(", ", mappingValue) + ") in class "
                                + bean.getClass().getName() + " can't has pattern as first value!"
                );
            }
        }

        for (String headPath : controllerValues) {
            for (String mappedPath : mappingValues) {
                paths.add(headPath.toLowerCase() + mappedPath.toLowerCase());
            }
        }

        for (String path : paths) {
            final var handlerMapping = new HandlerMapping(mapping.event(), path);
            final var handlerMethod = new TelegramMessageHandlerMethod(handlerMapping, bean, method);
            addHandlerMethod(path, handlerMethod);
        }

    }

    public void addHandlerMethod(String path, TelegramMessageHandlerMethod handlerMethod) {
        final HandlerMapping mapping = handlerMethod.getMapping();
        final Method mtd = handlerMethod.getMethod();
        final MessageType[] eventTypes = mapping.messageTypes();

        final PathMatcher pathMatcher = getPathMatcher();
        for (MessageType messageType : eventTypes) {
            final boolean canHasPath = messageType.canHasPath();
            if (canHasPath) {
                log.trace("Adding method handler for message type {} with path: {}", messageType, path);
                if (pathMatcher.isPattern(path)) {
                    final var prev = patternMap.get(messageType).putIfAbsent(path, handlerMethod);
                    if (prev != null) {
                        throw new IllegalStateException(
                                "Cant register handler with pattern mapping '" + path
                                        + "' for method '" + mtd.getName() + "'"
                        );
                    }
                } else {
                    final var prev = directMap.get(messageType).putIfAbsent(path, handlerMethod);
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

    @Nullable
    public TelegramMessageHandler findControllers(MessageType method, String command) {
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

    private PathMatcher getPathMatcher() {
        return pathMatchingConfigurer.getPathMatcher();
    }

    public PathMatchingConfigurer getPathMatchingConfigurer() {
        return pathMatchingConfigurer;
    }

    public void setPathMatchingConfigurer(PathMatchingConfigurer pathMatchingConfigurer) {
        this.pathMatchingConfigurer = pathMatchingConfigurer;
    }
}
