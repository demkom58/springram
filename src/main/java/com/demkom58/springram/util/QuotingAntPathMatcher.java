package com.demkom58.springram.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ant path matcher with custom tokenization of
 * quoted parts. It is useful for situations when
 * you need to ignore path separator.
 *
 * @author Max Demydenko
 * @since 0.2
 */
public class QuotingAntPathMatcher extends AntPathMatcher {
    private final Pattern quotePattern = Pattern.compile("\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"|(\\S+)");

    public QuotingAntPathMatcher() {
    }

    public QuotingAntPathMatcher(String pathSeparator) {
        super(pathSeparator);
    }

    @Override
    protected String[] tokenizePath(String path) {
        List<String> tokens = new ArrayList<>();

        Matcher m = quotePattern.matcher(path);
        while (m.find()) {
            final String found;

            if (m.group(1) != null) {
                found = m.group(1);
            } else {
                found = m.group(2);
            }

            if (!found.isBlank()) {
                tokens.add(found);
            }
        }

        return StringUtils.toStringArray(tokens);
    }
}
