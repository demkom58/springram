package com.demkom58.springram.controller.container;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.Chain;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.config.PathMatchingConfigurer;
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
import org.springframework.util.PathMatcher;

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

    record Type(@Nullable String throwableClassName, @Nullable MessageType type, @Nullable String chain, String value) {
        static final Type EMPTY_TYPE = new Type(null, null, null, "");
    }

    private static final Logger log = LoggerFactory.getLogger(CommandHandlerContainer.class);

    private final Map<Type, TelegramMessageHandler> typeHandlerMap = new HashMap<>();
    private PathMatchingConfigurer pathMatchingConfigurer = new PathMatchingConfigurer();

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
        final Set<String> paths = readPaths(beanClass, typeMapping, methodMapping);
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


    private Set<String> readPaths(Class<?> beanClass,
                                  @Nullable CommandMapping typeMapping,
                                  @Nullable CommandMapping methodMapping) {
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
            Type type = new Type(throwableClassName, messageType, chain, value);

            log.trace("Adding exception method handler for type {}", type);
            final boolean registered = typeHandlerMap.putIfAbsent(type, handlerMethod) == null;
            if (!registered) {
                throw new PathAlreadyTakenException("Cant register handler with type '"
                        + type + "' for method '" + handlerMethod.getMethod().getName() + "'");
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
        boolean methodNotNull = method != null;
        boolean valueNotEmpty = !value.isEmpty();

        TelegramMessageHandler handler = typeHandlerMap.get(new Type(ex.getName(), method, chain, value));
        if (handler == null && methodNotNull) {
            handler = typeHandlerMap.get(new Type(ex.getName(), null, chain, value));
        }

        if (handler == null && valueNotEmpty) {
            handler = typeHandlerMap.get(new Type(ex.getName(), method, chain, ""));
        }

        if (handler == null && methodNotNull && valueNotEmpty) {
            handler = typeHandlerMap.get(new Type(ex.getName(), null, chain, ""));
        }

        if (handler == null) {
            handler = typeHandlerMap.get(Type.EMPTY_TYPE);
        }

        return handler;
    }

    public TelegramMessageHandler getHandler(@Nullable Class<? extends Throwable> ex,
                                             @Nullable MessageType method,
                                             @Nullable String chain,
                                             String value) {
        return typeHandlerMap.get(new Type(ex == null ? null : ex.getName(), method, chain, value));
    }

    public PathMatchingConfigurer getPathMatchingConfigurer() {
        return pathMatchingConfigurer;
    }

    public void setPathMatchingConfigurer(PathMatchingConfigurer pathMatchingConfigurer) {
        this.pathMatchingConfigurer = pathMatchingConfigurer;
    }

}
