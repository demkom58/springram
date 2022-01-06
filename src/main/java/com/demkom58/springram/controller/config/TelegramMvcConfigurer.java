package com.demkom58.springram.controller.config;

import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolver;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;

import java.util.List;

public interface TelegramMvcConfigurer {

    default void configurePathMatcher(PathMatchingConfigurer configurer) {

    }

    default void configureArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolverList) {

    }

    default void configureReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }

}
