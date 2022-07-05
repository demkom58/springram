package com.demkom58.springram.controller.user;

import org.springframework.lang.Nullable;

/**
 * Retrieves springram user from some storage, can
 * be provided in {@link com.demkom58.springram.controller.config.SpringramConfigurer SpringramConfigurer}.
 *
 * @author Max Demydenko
 * @since 0.3
 */
public interface SpringramUserDetailsService {
    /**
     * Retrieves immutable {@link SpringramUserDetails SpringramUserDetails}.
     *
     * @param id telegram user identifier
     * @return springram user details instance
     */
    @Nullable
    SpringramUserDetails loadById(long id);
}
