package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.youngjun.auth.core.api.controller.v1.request.LoginRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

class RequestBodyUsernamePasswordAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val objectMapper: ObjectMapper,
    authenticationSuccessHandler: AuthenticationSuccessHandler,
    authenticationFailureHandler: AuthenticationFailureHandler,
) : AbstractAuthenticationProcessingFilter(LOGIN_REQUEST_MATCHER, authenticationManager) {
    init {
        setHandler(authenticationSuccessHandler, authenticationFailureHandler)
    }

    private fun setHandler(
        authenticationSuccessHandler: AuthenticationSuccessHandler,
        authenticationFailureHandler: AuthenticationFailureHandler,
    ) {
        setAuthenticationSuccessHandler(authenticationSuccessHandler)
        setAuthenticationFailureHandler(authenticationFailureHandler)
    }

    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Authentication {
        val loginRequest: LoginRequest =
            try {
                objectMapper.readValue(request.reader)
            } catch (ex: Exception) {
                throw BadCredentialsException(ex.message, ex)
            }
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username, loginRequest.password),
        )
    }

    companion object {
        private val LOGIN_REQUEST_MATCHER: AntPathRequestMatcher =
            AntPathRequestMatcher.antMatcher(
                HttpMethod.POST,
                "/auth/login",
            )
    }
}
