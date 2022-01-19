package com.demkom58.springram.controller.user;

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
     * @return non null, by default is "default" value
     */
    String getChain();
}
