package com.demkom58.springram.controller.container;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.Chain;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.annotation.ExceptionHandler;
import com.demkom58.springram.controller.method.HandlerMapping;
import com.demkom58.springram.controller.method.TelegramMessageHandler;
import com.demkom58.springram.controller.method.TelegramMessageHandlerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Container for searching exception handler methods
 * and registering them.
 *
 * @author Max Demydenko
 * @since 0.5
 */
@Component
public class ExceptionHandlerContainer {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerContainer.class);

    private final ExceptionHandlerMap handlerMap = new ExceptionHandlerMap();

    /**
     * Registers exception handler method of the specified bean.
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

        final String[] exceptions = readExceptions(method);
        final String[] chains = readChains(beanClass, method);
        final Set<String> paths = readPaths(typeMapping, methodMapping);
        final MessageType[] events = readMessageTypes(typeMapping, methodMapping);

        for (String ex : exceptions) {
            for (String chain : chains) {
                for (String path : paths) {
                    final var handlerMapping = new HandlerMapping(events, chain, path);
                    final var handlerMethod = new TelegramMessageHandlerMethod(handlerMapping, bean, method);

                    log.trace("Adding handler method for exception " + ex + " from class " + bean.getClass().getName());
                    addHandlerMethod(ex, handlerMethod, chain, path);
                }
            }
        }
    }

    private Set<String> readPaths(@Nullable CommandMapping typeMapping, @Nullable CommandMapping methodMapping) {
        if (methodMapping == null) {
            return Collections.singleton("");
        }

        final Set<String> paths = new HashSet<>();
        final String[] mappingValues = ObjectUtils.isEmpty(methodMapping.value()) ? new String[]{""} : methodMapping.value();

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

        if (paths.isEmpty()) {
            paths.add("");
        }

        return paths;
    }

    private MessageType[] readMessageTypes(@Nullable CommandMapping typeMapping,
                                           @Nullable CommandMapping methodMapping) {
        MessageType[] events = methodMapping == null ? null : methodMapping.event();

        if (ObjectUtils.isEmpty(events) && typeMapping != null) {
            events = typeMapping.event();
        }

        if (ObjectUtils.isEmpty(events)) {
            return new MessageType[]{};
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

    private String[] readExceptions(Method method) {
        final ExceptionHandler annotation = AnnotationUtils.findAnnotation(method, ExceptionHandler.class);
        assert annotation != null;

        Class<? extends Throwable>[] exceptions = annotation.value();
        if (exceptions.length == 0) {
            return new String[]{null};
        }

        String[] names = new String[exceptions.length];
        for (int i = 0; i < exceptions.length; i++) {
            names[i] = exceptions[i].getName();
        }

        return names;
    }

    private void addHandlerMethod(@Nullable String throwableClassName,
                                  TelegramMessageHandlerMethod handlerMethod,
                                  @Nullable String chain,
                                  String value) throws IllegalStateException {
        MessageType[] types = handlerMethod.getMapping().messageTypes();
        if (types.length == 0) {
            types = new MessageType[]{null};
        }

        for (MessageType messageType : types) {
            log.trace("Adding exception method handler for (throwable: {}, type: {}, chain: {}, path: {})",
                    throwableClassName, messageType, chain, value);
            final boolean registered = handlerMap.put(throwableClassName, messageType, chain, value, handlerMethod);
            if (!registered) {
                throw new PathAlreadyTakenException("Cant register handler method '"
                        + handlerMethod.getMethod().getName() + "'");
            }
        }
    }

    /**
     * Finds exception method handler for specified
     * method and command string.
     *
     * @param ex     exception class
     * @param method method of incoming command
     * @return method handler, that can be null
     */
    @Nullable
    public TelegramMessageHandler findHandler(Class<? extends Throwable> ex,
                                              @Nullable MessageType method,
                                              @Nullable String chain,
                                              String value) {
        return handlerMap.get(ex.getName(), method, chain, value);
    }

}
