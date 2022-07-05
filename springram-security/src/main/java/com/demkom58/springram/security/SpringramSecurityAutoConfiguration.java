package com.demkom58.springram.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableAspectJAutoProxy
@Import({BasicSpringramGrantedAuthoritiesProvider.class, SpringramMethodSecurityAspect.class})
public class SpringramSecurityAutoConfiguration {
}
