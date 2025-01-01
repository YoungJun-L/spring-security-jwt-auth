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
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthDetailsExchangeFilter(
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
        val authDetails = AuthDetails.from(authentication)
        val token = objectMapper.writeValueAsString(authDetails)
        val authRequest = AuthRequest(request, token)
        SecurityContextHolder.clearContext()
        filterChain.doFilter(authRequest, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest) = NotFilterRequestMatcher.matchers().any { it.matches(request) }

    private class AuthRequest(
        private val request: HttpServletRequest,
        private val token: String,
        private val cookies: Array<Cookie> = (request.cookies ?: emptyArray<Cookie>()) + Cookie("user", token),
    ) : HttpServletRequestWrapper(request)

    private data class AuthDetails(
        val id: Any,
        val details: Any,
    ) {
        companion object {
            fun from(authentication: Authentication): AuthDetails =
                AuthDetails(
                    if (authentication is AnonymousAuthenticationToken) -1L else authentication.principal,
                    authentication.details,
                )
        }
    }
}
