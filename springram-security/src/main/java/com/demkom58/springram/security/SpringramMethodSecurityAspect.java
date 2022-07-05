package com.demkom58.springram.security;

import com.demkom58.springram.controller.message.ChatMessage;
import com.demkom58.springram.controller.message.SpringramMessage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Aspect
@Component
public class SpringramMethodSecurityAspect {
    private final Collection<SpringramGrantedAuthoritiesProvider> providers;

    public SpringramMethodSecurityAspect(Collection<SpringramGrantedAuthoritiesProvider> providers) {
        this.providers = providers;
    }

    @Before("execution(* com.demkom58.springram.controller.method.TelegramMessageHandler.invoke(..))")
    public void setupSecurityContext(JoinPoint joinPoint) {
        SpringramMessage message = (SpringramMessage) joinPoint.getArgs()[0];
        AbsSender bot = (AbsSender) joinPoint.getArgs()[1];

        User user = message.getFromUser();
        Chat chat = null;
        if (message instanceof ChatMessage cm) {
            chat = cm.getChat();
        }

        final UserActionContext context = new UserActionContext(user, chat, message, bot);

        Collection<GrantedAuthority> authorities = getGrantedAuthorities(context);
        SpringramAuthentication authentication = new SpringramAuthentication(context, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(UserActionContext context) {
        if (providers.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<GrantedAuthority> authorities = null;
        for (SpringramGrantedAuthoritiesProvider provider : providers) {
            if (authorities == null) {
                authorities = provider.authorities(context);
            } else {
                authorities.addAll(provider.authorities(context));
            }
        }

        return authorities;
    }

}
