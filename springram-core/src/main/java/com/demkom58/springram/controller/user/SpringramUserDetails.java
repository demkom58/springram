package com.demkom58.springram.controller.user;

import org.springframework.lang.Nullable;

/**
 * Immutable representation of springram user,
 * for working with user related data in customizable
 * storage.
 *
 * @author Max Demydenko
 * @since 0.3
 */
public interface SpringramUserDetails {
    /**
     * Returns user chain retrieved on creation.
     *
     * @return identifier of chain, default chain is null
     */
    @Nullable
    String getChain();
}
