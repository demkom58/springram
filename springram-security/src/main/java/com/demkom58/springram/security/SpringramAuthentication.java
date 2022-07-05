package com.demkom58.springram.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.Collection;

public class SpringramAuthentication implements Authentication {
    private final UserActionContext context;
    private final Collection<? extends GrantedAuthority> authorities;

    public SpringramAuthentication(UserActionContext context, Collection<? extends GrantedAuthority> authorities) {
        this.context = context;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserActionContext getDetails() {
        return context;
    }

    @Override
    public Object getPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        Chat chat = context.chat();
        return context.user().getUserName() + '@' + (chat == null ? "null" : chat.getUserName());
    }
}
