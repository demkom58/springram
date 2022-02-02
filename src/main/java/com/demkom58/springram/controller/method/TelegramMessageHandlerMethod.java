package com.demkom58.springram.controller.method;

import com.demkom58.springram.controller.message.SpringramMessage;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.lang.reflect.Method;
import java.util.Arrays;

public class TelegramMessageHandlerMethod implements TelegramMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(TelegramMessageHandlerMethod.class);
    private static final Object[] EMPTY_ARGS = new Object[0];

    private final HandlerMapping mapping;
    private final Object bean;
    private final Method method;
    private final Method protoMethod;

    private final MethodParameter[] parameters;
    private final MethodParameter returnType;

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public TelegramMessageHandlerMethod(HandlerMapping mapping, Object bean, Method method) {
        this.mapping = mapping;
        this.bean = bean;
        this.method = method;
        this.protoMethod = BridgeMethodResolver.findBridgedMethod(method);
        ReflectionUtils.makeAccessible(protoMethod);
        this.parameters = methodParameters(method);
        this.returnType = new MethodParameter(method, -1);
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    public Object invoke(HandlerMethodArgumentResolverComposite resolvers,
                         SpringramMessage message,
                         AbsSender bot,
                         Object... providedArgs) throws Exception {
        Object[] args = getMethodArgumentValues(resolvers, message, bot, providedArgs);
        if (log.isTraceEnabled()) {
            log.trace("Arguments: {}", Arrays.toString(args));
        }

        return doInvoke(args);
    }

    protected Object[] getMethodArgumentValues(HandlerMethodArgumentResolverComposite resolvers,
                                               SpringramMessage message,
                                               AbsSender bot,
                                               Object... providedArgs) throws Exception {
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }

        Object[] foundArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);

            foundArgs[i] = findArgument(parameter, providedArgs);
            if (foundArgs[i] != null) {
                continue;
            }

            if (!resolvers.isSupported(parameter)) {
                throw new IllegalStateException("Resolver supporting '" + parameter + "' not found!");
            }

            try {
                foundArgs[i] = resolvers.resolve(parameter, message, bot);
            } catch (Exception ex) {
                log.debug("Failed to resolve parameter {}", parameter, ex);
                throw ex;
            }

        }
        return foundArgs;
    }

    @Nullable
    protected Object doInvoke(Object... args) throws Exception {
        try {
            return protoMethod.invoke(bean, args);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex.getMessage() != null ? ex.getMessage() : "Illegal arguments passed", ex);
        }
    }

    @Override
    public HandlerMapping getMapping() {
        return mapping;
    }

    public Method getMethod() {
        return method;
    }

    public Method getProtoMethod() {
        return protoMethod;
    }

    @Override
    public MethodParameter getReturnType() {
        return returnType;
    }

    private static MethodParameter[] methodParameters(Method method) {
        final int parameterCount = method.getParameterCount();
        final MethodParameter[] params = new MethodParameter[parameterCount];
        for (int i = 0; i < parameterCount; i++) {
            params[i] = new MethodParameter(method, i);
        }

        return params;
    }

    @Nullable
    protected static Object findArgument(MethodParameter parameter, @Nullable Object... providedArgs) {
        if (!ObjectUtils.isEmpty(providedArgs)) {
            final Class<?> parameterType = parameter.getParameterType();

            for (Object providedArg : providedArgs) {
                if (parameterType.isInstance(providedArg)) {
                    return providedArg;
                }
            }

        }

        return null;
    }

}
