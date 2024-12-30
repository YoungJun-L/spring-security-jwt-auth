package com.youngjun.auth.core.api.security.filter;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

public enum NotFilterRequestMatcher {

    REGISTER(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/auth/register")),
    REISSUE(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/auth/token")),
    H2_CONSOLE(AntPathRequestMatcher.antMatcher("/h2-console/**")),
    API_DOCS(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/docs/**")),
    HEALTH(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/health"));

    private final RequestMatcher matcher;

    NotFilterRequestMatcher(RequestMatcher matcher) {
        this.matcher = matcher;
    }

    public static RequestMatcher[] matchers() {
        return Arrays.stream(NotFilterRequestMatcher.values()).map(a -> a.matcher).toArray(RequestMatcher[]::new);
    }

}
