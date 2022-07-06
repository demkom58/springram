package com.demkom58.springram.security;

import com.demkom58.springram.controller.UserActionContext;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface SpringramGrantedAuthoritiesProvider {
    Collection<GrantedAuthority> authorities(UserActionContext context);
}
