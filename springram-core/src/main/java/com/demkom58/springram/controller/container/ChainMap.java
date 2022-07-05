package com.demkom58.springram.controller.container;

import com.demkom58.springram.controller.config.PathMatchingConfigurer;
import com.demkom58.springram.controller.method.TelegramMessageHandler;
import org.springframework.lang.Nullable;
import org.springframework.util.PathMatcher;

import java.util.*;

/**
 * Class to work with handlers maps in way
 * like multikey storage, without rewrites.
 *
 * @author Max Demydenko
 * @since 0.3
 */
class ChainMap {
    private final Map<String, Map<String, TelegramMessageHandler>> chainDirectMap = new HashMap<>();
    private final Map<String, Map<String, TelegramMessageHandler>> chainPatternMap = new HashMap<>();
    private final PathMatchingConfigurer matchingConfigurer;

    ChainMap(PathMatchingConfigurer matchingConfigurer) {
        this.matchingConfigurer = matchingConfigurer;
    }

    /**
     * Puts a handler to chain with specified path, when
     * doesn't exist registered handler with same path.
     *
     * @param chain   name of the chain
     * @param path    handled by method handler path
     * @param handler method handler instance
     * @return true if registered, false in case when path already taken
     */
    public boolean put(@Nullable String chain, String path, TelegramMessageHandler handler) {
        final PathMatcher pathMatcher = this.matchingConfigurer.getPathMatcher();
        final var registryMap = pathMatcher.isPattern(path) ? chainPatternMap : chainDirectMap;
        final var prev = registryMap
                .computeIfAbsent(chain, k -> new HashMap<>())
                .putIfAbsent(path, handler);
        return prev == null;
    }

    /**
     * Searches method handler for specified chain
     * and command.
     *
     * @param chain   name of chain in which search handler
     * @param command text of command that handler method handles
     * @return handler instance or null
     */
    @Nullable
    public TelegramMessageHandler get(@Nullable String chain, @Nullable String command) {
        final Map<String, TelegramMessageHandler> chainedDirect = chainDirectMap.get(chain);
        if (command == null) {
            command = "";
        }

        // handle not pattern command
        if (chainedDirect != null) {
            final var directHandler = chainedDirect.get(command.toLowerCase());
            if (directHandler != null) {
                return directHandler;
            }

            if (command.isEmpty()) {
                return null;
            }
        }

        // handle pattern command
        final PathMatcher pathMatcher = this.matchingConfigurer.getPathMatcher();
        final List<TelegramMessageHandler> handlers = new ArrayList<>();
        final Map<String, TelegramMessageHandler> chainedPattern = chainPatternMap.get(chain);

        if (chainedPattern != null) {
            for (Map.Entry<String, TelegramMessageHandler> entry : chainedPattern.entrySet()) {
                final String key = entry.getKey();
                if (pathMatcher.match(key, command)) {
                    handlers.add(entry.getValue());
                }
            }
        }

        // return default handler if not found
        if (handlers.isEmpty()) {
            return chainedDirect == null ? null : chainedDirect.get("");
        }

        // find handler with most specific path
        final Comparator<String> patternComparator = pathMatcher.getPatternComparator(command);
        handlers.sort((c1, c2) -> patternComparator.compare(
                c1.getMapping().value(),
                c2.getMapping().value()
        ));

        return handlers.get(0);
    }
}
