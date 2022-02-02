package com.demkom58.springram.controller.method.argument;

import com.demkom58.springram.controller.message.SpringramMessage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Composite {@link HandlerMethodArgumentResolver HandlerMethodArgumentResolver}
 * that searches first supported resolver,
 * caches it and returns resolved argument.
 *
 * @author Max Demydenko
 * @since 0.1
 */
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {
    private final List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
    private final Map<MethodParameter, HandlerMethodArgumentResolver> cachedResolvers = new ConcurrentHashMap<>();

    public void add(HandlerMethodArgumentResolver resolver) {
        this.resolvers.add(resolver);
    }

    public void addAll(HandlerMethodArgumentResolver... resolvers) {
        this.resolvers.addAll(Arrays.asList(resolvers));
    }

    public void addAll(Collection<HandlerMethodArgumentResolver> resolvers) {
        this.resolvers.addAll(resolvers);
    }

    @Override
    public boolean isSupported(MethodParameter parameter) {
        return getArgumentResolver(parameter) != null;
    }

    @Override
    @Nullable
    public Object resolve(MethodParameter parameter, SpringramMessage message, AbsSender bot) throws Exception {
        final HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
        if (resolver == null) {
            throw new IllegalArgumentException(
                    "Unsupported parameter type '" + parameter.getParameterType().getName() + "'"
            );
        }

        return resolver.resolve(parameter, message, bot);
    }

    @Nullable
    private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        final HandlerMethodArgumentResolver cached = cachedResolvers.get(parameter);

        if (cached == null) {
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                if (resolver.isSupported(parameter)) {
                    cachedResolvers.put(parameter, resolver);
                    return resolver;
                }
            }
        }

        return cached;
    }
}
