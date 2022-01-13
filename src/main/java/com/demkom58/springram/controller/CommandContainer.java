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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Container for searching handler methods and registering.
 *
 * @author Max Demydenko
 * @since 0.1
 */
@Component
public class CommandContainer {
    private static final Logger log = LoggerFactory.getLogger(CommandContainer.class);
    private static final MessageType[] TEXT_MESSAGE_EVENTS  = new MessageType[]{MessageType.TEXT_MESSAGE};

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

    /**
     * Registers handler method of the specified bean.
     *
     * @param bean   that owns method
     * @param method the handler method that should be registered
     */
    public void addMethod(Object bean, Method method) {
        final Class<?> beanClass = bean.getClass();

        final BotController controller = AnnotationUtils.findAnnotation(beanClass, BotController.class);
        assert controller != null;

        final CommandMapping typeMapping = AnnotationUtils.findAnnotation(beanClass, CommandMapping.class);
        final CommandMapping methodMapping = AnnotationUtils.findAnnotation(method, CommandMapping.class);
        assert methodMapping != null;

        final Set<String> paths = new HashSet<>();

        final String[] mappingValues
                = ObjectUtils.isEmpty(methodMapping.value()) ? new String[]{""} : methodMapping.value();

        if (typeMapping != null) {
            for (String headPath : typeMapping.value()) {
                for (String mappedPath : mappingValues) {
                    final String ltHeadPath = headPath.toLowerCase().trim();
                    final String ltMappedPath = mappedPath.toLowerCase().trim();
                    paths.add(ltHeadPath + " " + ltMappedPath);
                }
            }
        } else {
            for (String mappedPath : mappingValues) {
                paths.add(mappedPath.toLowerCase());
            }
        }

        final PathMatcher pathMatcher = pathMatchingConfigurer.getPathMatcher();
        for (String mappingValue : paths) {
            final String[] cmd = mappingValue.split(" ", 2);
            final boolean isPattern = pathMatcher.isPattern(cmd[0]);
            if (isPattern) {
                throw new IllegalArgumentException(
                        "CommandMapping method with mappings (" + String.join(", ", mappingValue) + ") in class "
                                + beanClass.getName() + " can't has pattern as first value!"
                );
            }
        }

        if (paths.isEmpty()) {
            paths.add("");
        }

        for (String path : paths) {
            MessageType[] events = methodMapping.event();

            if (ObjectUtils.isEmpty(events) && typeMapping != null) {
                events = typeMapping.event();
            }

            if (ObjectUtils.isEmpty(events)) {
                events = TEXT_MESSAGE_EVENTS;
            }

            System.out.println("{path: \"" + path + "\", events: " + Arrays.toString(events) + "}");

            final var handlerMapping = new HandlerMapping(events, path);
            final var handlerMethod = new TelegramMessageHandlerMethod(handlerMapping, bean, method);
            addHandlerMethod(path, handlerMethod);
        }

    }

    private void addHandlerMethod(String path,
                                  TelegramMessageHandlerMethod handlerMethod) throws IllegalStateException {
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

    /**
     * Finds method handler for specified
     * method and command string.
     *
     * @param method  method of incomming command
     * @param command text of command
     * @return method handler, that can be null
     */
    @Nullable
    public TelegramMessageHandler findHandler(MessageType method, String command) {
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
