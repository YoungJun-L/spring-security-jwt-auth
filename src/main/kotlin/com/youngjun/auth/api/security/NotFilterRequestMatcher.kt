package com.youngjun.auth.api.security

import org.springframework.http.HttpMethod
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

enum class NotFilterRequestMatcher(
    private val matcher: RequestMatcher,
) {
    REGISTER(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/auth/register")),
    REISSUE(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/auth/token")),
    H2_CONSOLE(AntPathRequestMatcher.antMatcher("/h2-console/**")),
    API_DOCS(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/docs/**")),
    HEALTH(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/health")),
    ;

    companion object {
        fun matchers(): Array<RequestMatcher> = entries.map { it.matcher }.toTypedArray()
    }
}
