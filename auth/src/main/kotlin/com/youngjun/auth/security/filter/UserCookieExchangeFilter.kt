package com.youngjun.auth.security.filter

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.security.config.NotFilterRequestMatcher
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class UserCookieExchangeFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authentication =
            SecurityContextHolder.getContext().authentication
                ?: throw AuthenticationServiceException("Authentication object should not be null.")
        SecurityContextHolder.clearContext()
        filterChain.doFilter(UserRequest(request, (authentication.principal as Account).id), response)
    }

    override fun shouldNotFilter(request: HttpServletRequest) = NotFilterRequestMatcher.matchers().any { it.matches(request) }

    private class UserRequest(
        private val request: HttpServletRequest,
        private val userId: Long,
    ) : HttpServletRequestWrapper(request) {
        override fun getCookies(): Array<Cookie> = (request.cookies ?: emptyArray<Cookie>()) + Cookie("USER_ID", userId.toString())
    }
}
