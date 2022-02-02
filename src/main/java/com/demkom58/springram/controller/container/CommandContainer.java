package com.demkom58.springram.controller.container;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.Chain;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Container for searching handler methods and registering.
 *
 * @author Max Demydenko
 * @since 0.1
 */
@Component
public class CommandContainer {
    private static final Logger log = LoggerFactory.getLogger(CommandContainer.class);
    private static final MessageType[] TEXT_MESSAGE_EVENTS = new MessageType[]{MessageType.TEXT_MESSAGE};

    private final Map<MessageType, ChainMap> typeHandlerMap;
    private PathMatchingConfigurer pathMatchingConfigurer = new PathMatchingConfigurer();

    public CommandContainer() {
        final Map<MessageType, ChainMap> map = new HashMap<>();
        for (MessageType value : MessageType.values()) {
            map.put(value, new ChainMap(pathMatchingConfigurer));
        }

        this.typeHandlerMap = Maps.newEnumMap(map);
    }

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

        final String[] chains = readChains(beanClass, method);
        final Set<String> paths = readPaths(beanClass, typeMapping, methodMapping);
        final MessageType[] events = readMessageTypes(typeMapping, methodMapping);

        for (String chain : chains) {
            for (String path : paths) {
                final var handlerMapping = new HandlerMapping(events, chain, path);
                final var handlerMethod = new TelegramMessageHandlerMethod(handlerMapping, bean, method);
                addHandlerMethod(chain, path, handlerMethod);
            }
        }
    }

    private Set<String> readPaths(Class<?> beanClass,
                                  CommandMapping typeMapping, CommandMapping methodMapping) {
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

        return paths;
    }

    private MessageType[] readMessageTypes(CommandMapping typeMapping, CommandMapping methodMapping) {
        MessageType[] events = methodMapping.event();

        if (ObjectUtils.isEmpty(events) && typeMapping != null) {
            events = typeMapping.event();
        }

        if (ObjectUtils.isEmpty(events)) {
            events = TEXT_MESSAGE_EVENTS;
        }

        return events;
    }

    private String[] readChains(Class<?> beanClass, Method method) {
        final Chain classChainAnnotation = AnnotationUtils.findAnnotation(beanClass, Chain.class);
        final Chain methodChainAnnotation = AnnotationUtils.findAnnotation(method, Chain.class);

        String[] chains = {};
        if (methodChainAnnotation != null && !ObjectUtils.isEmpty(methodChainAnnotation.chain())) {
            chains = methodChainAnnotation.chain();
        } else if (classChainAnnotation != null && !ObjectUtils.isEmpty(classChainAnnotation.chain())) {
            chains = classChainAnnotation.chain();
        }

        if (chains.length == 0) {
            chains = new String[]{null};
        }

        return chains;
    }

    private void addHandlerMethod(@Nullable String chain, String path,
                                  TelegramMessageHandlerMethod handlerMethod) throws IllegalStateException {
        final MessageType[] types = handlerMethod.getMapping().messageTypes();
        for (MessageType type : types) {
            log.trace("Adding method handler for message type {} with path: {}", type, path);
            final boolean registered = typeHandlerMap.get(type).put(chain, path, handlerMethod);
            if (!registered) {
                throw new PathAlreadyTakenException("Cant register handler with direct mapping '" + path
                        + "' in chain '" + chain + "' for method '" + handlerMethod.getMethod().getName() + "'");
            }
        }
    }

    /**
     * Finds method handler for specified
     * method and command string.
     *
     * @param method  method of incoming command
     * @param command text of command
     * @return method handler, that can be null
     */
    @Nullable
    public TelegramMessageHandler findHandler(MessageType method, String chain, String command) {
        return typeHandlerMap.get(method).get(chain, command);
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
