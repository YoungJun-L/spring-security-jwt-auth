package com.youngjun.auth.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.youngjun.auth.security.support.error.TypedAuthenticationException
import com.youngjun.auth.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

class JsonLoginAuthenticationFilter(
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
                throw TypedAuthenticationException(ErrorType.BAD_REQUEST_ERROR, ex)
            }
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.toEmailAddress(), loginRequest.toRawPassword().value),
        )
    }

    companion object {
        private val LOGIN_REQUEST_MATCHER: AntPathRequestMatcher = AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/auth/login")
    }
}
