package com.demkom58.springram.controller.container;

/**
 * Exception that should be thrown when trying
 * register handler with path in context when
 * already exists handler with same path.
 *
 * @author Max Demydenko
 * @since 0.3
 */
public class PathAlreadyTakenException extends RuntimeException {
    public PathAlreadyTakenException() {
    }

    public PathAlreadyTakenException(String message) {
        super(message);
    }

    public PathAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }

    public PathAlreadyTakenException(Throwable cause) {
        super(cause);
    }

    public PathAlreadyTakenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
