package com.demkom58.springram.security;

import com.demkom58.springram.controller.UserActionContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class BasicSpringramGrantedAuthoritiesProvider implements SpringramGrantedAuthoritiesProvider {
    @Override
    public Collection<GrantedAuthority> authorities(UserActionContext context) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        final User user = context.user();

        Boolean isBot = user.getIsBot();
        if (isBot) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TG_BOT"));
        }

        Boolean isPremium = user.getIsPremium();
        if (isPremium != null && isPremium) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TG_PREMIUM"));
        }

        return authorities;
    }
}
