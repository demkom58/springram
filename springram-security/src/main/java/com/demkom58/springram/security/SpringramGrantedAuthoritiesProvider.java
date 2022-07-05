package com.demkom58.springram.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface SpringramGrantedAuthoritiesProvider {
    Collection<GrantedAuthority> authorities(UserActionContext context);
}
