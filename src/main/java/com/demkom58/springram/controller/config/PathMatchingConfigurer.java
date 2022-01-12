package com.demkom58.springram.controller.config;

import com.demkom58.springram.util.QuotingAntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Special class for configuration path matching for commands,
 * is used in {@link TelegramMvcConfigurer TelegramMvcConfigurer}.
 *
 * @author Max Demydenko
 * @since 0.2
 */
public class PathMatchingConfigurer {
    private boolean commandSlashMatch = true;
    private PathMatcher pathMatcher;

    public PathMatchingConfigurer() {
        QuotingAntPathMatcher pathMatcher = new QuotingAntPathMatcher(" ");
        pathMatcher.setCaseSensitive(false);
        this.pathMatcher = pathMatcher;
    }

    /**
     * Sets slash matching in command, if enabled slash will be ignored,
     * so you will not need to write it in command paths.
     *
     * @param commandSlashMatch specifies new state of slash matching
     */
    public void setCommandSlashMatch(boolean commandSlashMatch) {
        this.commandSlashMatch = commandSlashMatch;
    }

    /**
     * If enabled slash in command will be ignored,
     * so you will not need to write it in command paths.
     *
     * @return slash matching state
     */
    public boolean isCommandSlashMatch() {
        return commandSlashMatch;
    }

    /**
     * Allows set default path matcher, by default it is
     * {@link QuotingAntPathMatcher QuotingAntPathMatcher}.
     *
     * @param pathMatcher is {@link PathMatcher PathMatcher} that you want to set
     */
    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * Allows getting current path matcher that is used in
     * path matching for commands.
     *
     * @return current path matcher
     */
    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }
}
