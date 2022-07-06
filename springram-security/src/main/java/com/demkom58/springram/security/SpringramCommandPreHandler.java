package com.demkom58.springram.security;

import com.demkom58.springram.controller.CommandPreHandler;
import com.demkom58.springram.controller.UserActionContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class SpringramCommandPreHandler implements CommandPreHandler {
    private final Collection<SpringramGrantedAuthoritiesProvider> providers;

    public SpringramCommandPreHandler(Collection<SpringramGrantedAuthoritiesProvider> providers) {
        this.providers = providers;
    }

    @Override
    public void handle(UserActionContext context) {
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
