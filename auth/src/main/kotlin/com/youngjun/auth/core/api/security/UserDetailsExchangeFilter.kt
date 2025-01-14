package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class UserDetailsExchangeFilter(
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authentication =
            SecurityContextHolder.getContext().authentication
                ?: throw AuthenticationServiceException("Authentication object should not be null.")
        val userRequest = UserRequest(request, objectMapper.writeValueAsString(AuthUserDetails.from(authentication)))
        SecurityContextHolder.clearContext()
        filterChain.doFilter(userRequest, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest) = NotFilterRequestMatcher.matchers().any { it.matches(request) }

    private class UserRequest(
        private val request: HttpServletRequest,
        private val token: String,
        private val cookies: Array<Cookie> = (request.cookies ?: emptyArray<Cookie>()) + Cookie("user", token),
    ) : HttpServletRequestWrapper(request) {
        override fun getCookies(): Array<Cookie> = cookies
    }

    private data class AuthUserDetails(
        val username: Any,
        val details: Any,
    ) {
        companion object {
            fun from(authentication: Authentication): AuthUserDetails =
                AuthUserDetails(
                    if (authentication is AnonymousAuthenticationToken) "" else authentication.principal,
                    authentication.details,
                )
        }
    }
}
