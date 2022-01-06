package com.demkom58.springram.controller.method.argument.impl;

import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolver;
import com.demkom58.springram.controller.message.TelegramMessage;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;

public class PathVariablesHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean isSupported(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PathVariable.class);
    }

    @Override
    @Nullable
    public Object resolve(MethodParameter parameter, TelegramMessage message, AbsSender bot) {
        final Object variablesObject = message.getAttribute("variables");

        if (variablesObject instanceof Map<?, ?> variablesMap) {
            final PathVariable annotation = parameter.getParameterAnnotation(PathVariable.class);
            assert annotation != null;

            String variableName = annotation.value();
            if (variableName.isEmpty()) {
                variableName = parameter.getParameterName();
            }

            return variablesMap.get(variableName);
        }

        return null;
    }
}
