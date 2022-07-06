package com.demkom58.springram.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({BasicSpringramGrantedAuthoritiesProvider.class, SpringramCommandPreHandler.class})
public class SpringramSecurityAutoConfiguration {
}
