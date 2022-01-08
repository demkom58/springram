package com.demkom58.springram.controller.config;

import com.demkom58.springram.util.QuotingAntPathMatcher;
import org.springframework.util.PathMatcher;

public class PathMatchingConfigurer {
    private boolean commandSlashMatch = true;
    private PathMatcher pathMatcher;

    public PathMatchingConfigurer() {
        QuotingAntPathMatcher pathMatcher = new QuotingAntPathMatcher(" ");
        pathMatcher.setCaseSensitive(false);
        this.pathMatcher = pathMatcher;
    }

    public void setCommandSlashMatch(boolean commandSlashMatch) {
        this.commandSlashMatch = commandSlashMatch;
    }

    public boolean isCommandSlashMatch() {
        return commandSlashMatch;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }
}
