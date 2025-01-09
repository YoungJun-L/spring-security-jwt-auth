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
        val userRequest = UserRequest(request, objectMapper.writeValueAsString(UserAuthDetails.from(authentication)))
        SecurityContextHolder.clearContext()
        filterChain.doFilter(userRequest, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest) = NotFilterRequestMatcher.matchers().any { it.matches(request) }

    private class UserRequest(
        private val request: HttpServletRequest,
        private val token: String,
        private val cookies: Array<Cookie> = (request.cookies ?: emptyArray<Cookie>()) + Cookie("user", token),
    ) : HttpServletRequestWrapper(request)

    private data class UserAuthDetails(
        private val id: Any,
        private val details: Any,
    ) {
        companion object {
            fun from(authentication: Authentication): UserAuthDetails =
                UserAuthDetails(
                    if (authentication is AnonymousAuthenticationToken) "" else authentication.principal,
                    authentication.details,
                )
        }
    }
}
