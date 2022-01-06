package com.demkom58.springram.controller.config;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class PathMatchingConfigurer {
    private boolean commandSlashMatch = true;
    private PathMatcher pathMatcher;

    public PathMatchingConfigurer() {
        AntPathMatcher pathMatcher = new AntPathMatcher(" ");
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
