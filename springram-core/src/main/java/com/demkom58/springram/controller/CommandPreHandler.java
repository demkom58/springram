package com.demkom58.springram.controller;

/**
 * Calls before command execution.
 *
 * @author Max Demydenko
 * @since 0.5
 */
public interface CommandPreHandler {
    /**
     * Calls before command execution.
     *
     * @param context telegram command execution context.
     */
    void handle(UserActionContext context);
}
