package com.demkom58.springram.controller;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.config.PathMatchingConfigurer;
import com.demkom58.springram.controller.config.TelegramMvcConfigurerComposite;
import com.demkom58.springram.controller.method.HandlerMapping;
import com.demkom58.springram.controller.method.TelegramMessageHandlerMethod;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolver;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.springram.controller.method.argument.impl.PathVariablesHandlerMethodArgumentResolver;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandlerComposite;
import com.demkom58.springram.controller.method.result.impl.SendMessageHandlerMethodReturnValueHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;

import java.lang.reflect.Method;
import java.util.*;

public class UpdateBeanPostProcessor implements BeanPostProcessor, Ordered {
    private final Map<String, Class<?>> botControllerMap = new HashMap<>();

    private final CommandContainer container;
    private final PathMatchingConfigurer pathMatchingConfigurer;

    public UpdateBeanPostProcessor(CommandContainer container, TelegramMvcConfigurerComposite configurerComposite) {
        final HandlerMethodArgumentResolverComposite argumentResolvers
                = new HandlerMethodArgumentResolverComposite();

        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        configurerComposite.configureArgumentResolvers(resolvers);
        resolvers.addAll(createArgumentResolvers());
        argumentResolvers.addAll(resolvers);

        final HandlerMethodReturnValueHandlerComposite returnValueHandlers
                = new HandlerMethodReturnValueHandlerComposite();

        List<HandlerMethodReturnValueHandler> returnHandlers = new ArrayList<>();
        configurerComposite.configureReturnValueHandlers(returnHandlers);
        returnHandlers.addAll(createReturnValueHandlers());
        returnValueHandlers.addAll(returnHandlers);

        pathMatchingConfigurer = new PathMatchingConfigurer();
        configurerComposite.configurePathMatcher(pathMatchingConfigurer);
        container.setPathMatchingConfigurer(pathMatchingConfigurer);

        this.container = container;
        container.setReturnValueHandlers(returnValueHandlers);
        container.setArgumentResolvers(argumentResolvers);
    }

    private List<HandlerMethodArgumentResolver> createArgumentResolvers() {
        return List.of(
                new PathVariablesHandlerMethodArgumentResolver()
        );
    }

    private List<HandlerMethodReturnValueHandler> createReturnValueHandlers() {
        return List.of(
                new SendMessageHandlerMethodReturnValueHandler()
        );
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> beanClass = bean.getClass();

        if (beanClass.isAnnotationPresent(BotController.class)) {
            botControllerMap.put(beanName, beanClass);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> original = botControllerMap.get(beanName);
        if (original == null) {
            return bean;
        }

        Arrays.stream(original.getMethods())
                .filter(method -> method.isAnnotationPresent(CommandMapping.class))
                .forEach((Method method) -> addHandlerMethod(bean, method));

        return bean;
    }

    private void addHandlerMethod(Object bean, Method method) {
        final BotController controller = bean.getClass().getAnnotation(BotController.class);
        final CommandMapping mapping = method.getAnnotation(CommandMapping.class);

        final Set<String> paths = new HashSet<>();

        final String[] controllerValues
                = ObjectUtils.isEmpty(controller.value()) ? new String[]{""} : controller.value();
        final String[] mappingValues
                = ObjectUtils.isEmpty(mapping.value()) ? new String[]{""} : mapping.value();

        final PathMatcher pathMatcher = pathMatchingConfigurer.getPathMatcher();
        for (String mappingValue : mappingValues) {
            final String[] cmd = mappingValue.split(" ", 2);
            final boolean isPattern = pathMatcher.isPattern(cmd[0]);
            if (isPattern) {
                throw new IllegalArgumentException(
                        "CommandMapping method with mappings (" + String.join(", ", mappingValue) + ") in class "
                        + bean.getClass().getName() + " can't has pattern as first value!"
                );
            }
        }

        for (String headPath : controllerValues) {
            for (String mappedPath : mappingValues) {
                paths.add(headPath.toLowerCase() + mappedPath.toLowerCase());
            }
        }

        for (String path : paths) {
            final var handlerMapping = new HandlerMapping(mapping.event(), path);
            final var handlerMethod = new TelegramMessageHandlerMethod(handlerMapping, bean, method);
            container.addBotController(path, handlerMethod);
        }

    }

    @Override
    public int getOrder() {
        return 100;
    }

}