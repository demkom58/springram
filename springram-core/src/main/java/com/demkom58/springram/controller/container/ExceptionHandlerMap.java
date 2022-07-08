package com.demkom58.springram.controller.container;

import com.demkom58.springram.controller.message.MessageType;
import com.demkom58.springram.controller.method.TelegramMessageHandlerMethod;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to work with exception handlers maps in way
 * like multikey storage and fallback keys, not allows rewrites.
 *
 * @author Max Demydenko
 * @since 0.6
 */
class ExceptionHandlerMap {
    private final HashMap<MessageType, Map<String, Map<String, Map<String, TelegramMessageHandlerMethod>>>>
            handlerMap = new HashMap<>();

    /**
     * Puts a handler, when here doesn't exist registered handler with same description.
     *
     * @param exception name of handled exception class
     * @param type      handled type
     * @param path      handled path
     * @param chain     name of the chain
     * @param handler   exception method handler instance
     * @return true if registered, false in case when already same handler registered
     */
    public boolean put(@Nullable String exception,
                       @Nullable MessageType type,
                       @Nullable String path,
                       String chain,
                       TelegramMessageHandlerMethod handler) {
        return handlerMap.compute(type, (k, v) -> new HashMap<>())
                .compute(path, (k, v) -> new HashMap<>())
                .compute(chain, (k, v) -> new HashMap<>())
                .putIfAbsent(exception, handler) == null;
    }

    /**
     * Searches exception method handler for specified description.
     *
     * @param exception name of handled exception class
     * @param type      handled type
     * @param path      raw text of command that handler method handles
     * @param chain     name of chain in which search handler
     * @return handler instance or null
     */
    @Nullable
    public TelegramMessageHandlerMethod get(@Nullable String exception,
                                            @Nullable MessageType type,
                                            @Nullable String path,
                                            String chain) {
        var types = getWithDefault(handlerMap, type, null);
        if (types == null) {
            return null;
        }

        var paths = getWithDefault(types, path, null);
        if (paths == null) {
            return null;
        }

        var chains = getWithDefault(paths, chain, "");
        if (chains == null) {
            return null;
        }

        return getWithDefault(chains, exception, null);
    }

    @Nullable
    private <K, V> V getWithDefault(Map<K, V> from, @Nullable K key, @Nullable K alternativeKey) {
        V v = from.get(key);

        if (key != null && v == null) {
            return from.get(alternativeKey);
        }

        return v;
    }
}
