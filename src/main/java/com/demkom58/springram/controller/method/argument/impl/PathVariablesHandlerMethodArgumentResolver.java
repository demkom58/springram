package com.demkom58.springram.controller.method.argument.impl;

import com.demkom58.springram.controller.annotation.PathVariable;
import com.demkom58.springram.controller.message.TelegramMessage;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolver;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
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

        final PathVariable annotation = parameter.getParameterAnnotation(PathVariable.class);
        assert annotation != null;

        String variableName = StringUtils.hasLength(annotation.value()) ? annotation.value() : annotation.name();
        if (variableName.isEmpty()) {
            variableName = parameter.getParameterName();
        }
        assert variableName != null;

        if (variablesObject instanceof Map<?, ?> variablesMap) {
            return variablesMap.get(variableName);
        }

        return null;
    }

}
