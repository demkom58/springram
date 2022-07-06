package com.demkom58.springram.security;

import com.demkom58.springram.controller.UserActionContext;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Provides granted authorities for user using
 * current telegram command execution context.
 *
 * @author Max Demydenko
 * @since 0.5
 */
public interface SpringramGrantedAuthoritiesProvider {
    /**
     * Provides granted authorities for user.
     *
     * @param context telegram command execution context.
     * @return granted authorities.
     */
    Collection<GrantedAuthority> authorities(UserActionContext context);
}
