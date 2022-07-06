package com.demkom58.springram.security;

import com.demkom58.springram.controller.CommandPreHandler;
import com.demkom58.springram.controller.UserActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class SpringramCommandPreHandler implements CommandPreHandler {
    private static final Logger log = LoggerFactory.getLogger(SpringramCommandPreHandler.class);

    private final Collection<SpringramGrantedAuthoritiesProvider> providers;

    public SpringramCommandPreHandler(Collection<SpringramGrantedAuthoritiesProvider> providers) {
        this.providers = providers;
        log.info("pre handler created");
    }

    @Override
    public void handle(UserActionContext context) {
        Collection<GrantedAuthority> authorities = getGrantedAuthorities(context);
        SpringramAuthentication authentication = new SpringramAuthentication(context, authorities);

        log.info("Authentication: " + authentication + " authorities: "
                + authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", ")));
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
