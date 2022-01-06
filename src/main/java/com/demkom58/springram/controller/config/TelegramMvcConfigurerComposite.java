package com.demkom58.springram.controller.config;

import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolver;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TelegramMvcConfigurerComposite implements TelegramMvcConfigurer {
    private final List<TelegramMvcConfigurer> configurers = new ArrayList<>();

    public void addAll(Collection<TelegramMvcConfigurer> mvcConfigurers) {
        configurers.addAll(mvcConfigurers);
    }

    @Override
    public void configurePathMatcher(PathMatchingConfigurer pathMatchingConfigurer) {
        for (TelegramMvcConfigurer configurer : configurers) {
            configurer.configurePathMatcher(pathMatchingConfigurer);
        }
    }

    @Override
    public void configureArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolverList) {
        for (TelegramMvcConfigurer configurer : configurers) {
            configurer.configureArgumentResolvers(argumentResolverList);
        }
    }

    @Override
    public void configureReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (TelegramMvcConfigurer configurer : configurers) {
            configurer.configureReturnValueHandlers(returnValueHandlers);
        }
    }
}
