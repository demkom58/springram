package com.demkom58.springram.controller.config;

import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolver;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpringramConfigurerComposite implements SpringramConfigurer {
    private final List<SpringramConfigurer> configurers = new ArrayList<>();

    public void addAll(Collection<SpringramConfigurer> mvcConfigurers) {
        configurers.addAll(mvcConfigurers);
    }

    @Override
    public void configurePathMatcher(PathMatchingConfigurer pathMatchingConfigurer) {
        for (SpringramConfigurer configurer : configurers) {
            configurer.configurePathMatcher(pathMatchingConfigurer);
        }
    }

    @Override
    public void configureArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolverList) {
        for (SpringramConfigurer configurer : configurers) {
            configurer.configureArgumentResolvers(argumentResolverList);
        }
    }

    @Override
    public void configureReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (SpringramConfigurer configurer : configurers) {
            configurer.configureReturnValueHandlers(returnValueHandlers);
        }
    }
}
