package com.demkom58.springram.controller;

import com.demkom58.springram.controller.annotation.BotController;
import com.demkom58.springram.controller.annotation.CommandMapping;
import com.demkom58.springram.controller.config.PathMatchingConfigurer;
import com.demkom58.springram.controller.config.SpringramConfigurerComposite;
import com.demkom58.springram.controller.container.CommandHandlerContainer;
import com.demkom58.springram.controller.container.ExceptionHandlerContainer;
import com.demkom58.springram.controller.annotation.ExceptionHandler;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolver;
import com.demkom58.springram.controller.method.argument.HandlerMethodArgumentResolverComposite;
import com.demkom58.springram.controller.method.argument.impl.PathVariablesHandlerMethodArgumentResolver;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandler;
import com.demkom58.springram.controller.method.result.HandlerMethodReturnValueHandlerComposite;
import com.demkom58.springram.controller.method.result.impl.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.*;

public class UpdateBeanPostProcessor implements BeanPostProcessor, Ordered {
    private final Map<String, Class<?>> botControllerMap = new HashMap<>();
    private final CommandHandlerContainer commandContainer;
    private final ExceptionHandlerContainer exceptionContainer;

    public UpdateBeanPostProcessor(TelegramCommandDispatcher commandDispatcher,
                                   CommandHandlerContainer commandContainer,
                                   ExceptionHandlerContainer exceptionContainer,
                                   SpringramConfigurerComposite configurerComposite) {
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

        commandDispatcher.setReturnValueHandlers(returnValueHandlers);
        commandDispatcher.setArgumentResolvers(argumentResolvers);

        this.commandContainer = commandContainer;
        this.exceptionContainer = exceptionContainer;

        PathMatchingConfigurer pathMatchingConfigurer = new PathMatchingConfigurer();
        configurerComposite.configurePathMatcher(pathMatchingConfigurer);

        commandContainer.setPathMatchingConfigurer(pathMatchingConfigurer);
    }

    private List<HandlerMethodArgumentResolver> createArgumentResolvers() {
        return List.of(
                new PathVariablesHandlerMethodArgumentResolver()
        );
    }

    private List<HandlerMethodReturnValueHandler> createReturnValueHandlers() {
        return List.of(
                new BotApiMethodHandlerMethodReturnValueHandler(),
                new AddStickerToSetHandlerMethodReturnValueHandler(),
                new CreateNewStickerSetHandlerMethodReturnValueHandler(),
                new EditMessageMediaHandlerMethodReturnValueHandler(),
                new SendAnimationHandlerMethodReturnValueHandler(),
                new SendAudioHandlerMethodReturnValueHandler(),
                new SendDocumentHandlerMethodReturnValueHandler(),
                new SendMediaGroupHandlerMethodReturnValueHandler(),
                new SendPhotoHandlerMethodReturnValueHandler(),
                new SendPollHandlerMethodReturnValueHandler(),
                new SendStickerHandlerMethodReturnValueHandler(),
                new SendVideoHandlerMethodReturnValueHandler(),
                new SendVideoNoteHandlerMethodReturnValueHandler(),
                new SendVoiceHandlerMethodReturnValueHandler(),
                new SetChatPhotoHandlerMethodReturnValueHandler(),
                new SetStickerSetThumbHandlerMethodReturnValueHandler(),
                new UploadStickerFileHandlerMethodReturnValueHandler()
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
                .filter(method -> method.isAnnotationPresent(CommandMapping.class)
                        && !method.isAnnotationPresent(ExceptionHandler.class))
                .forEach((Method method) -> commandContainer.addMethod(bean, method));

        Arrays.stream(original.getMethods())
                .filter(method -> method.isAnnotationPresent(ExceptionHandler.class))
                .forEach(method -> exceptionContainer.addMethod(bean, method));

        return bean;
    }

    @Override
    public int getOrder() {
        return 100;
    }

}