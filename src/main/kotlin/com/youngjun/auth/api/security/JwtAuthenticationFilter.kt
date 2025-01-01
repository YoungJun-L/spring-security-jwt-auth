package com.youngjun.auth.api.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val bearerTokenResolver: BearerTokenResolver,
    private val authenticationManager: AuthenticationManager,
    private val authenticationFailureHandler: AuthenticationFailureHandler,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val token = bearerTokenResolver.resolve(request)
            if (token == null) {
                filterChain.doFilter(request, response)
                return
            }
            val authRequest = BearerTokenAuthenticationToken(token)
            val authResult = authenticationManager.authenticate(authRequest)
            successfulAuthentication(authResult)
        } catch (ex: AuthenticationException) {
            unsuccessfulAuthentication(request, response, ex)
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun successfulAuthentication(authResult: Authentication) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)
    }

    private fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException,
    ) {
        SecurityContextHolder.clearContext()
        authenticationFailureHandler.onAuthenticationFailure(request, response, failed)
    }

    override fun shouldNotFilter(request: HttpServletRequest) = NotFilterRequestMatcher.matchers().any { it.matches(request) }
}
