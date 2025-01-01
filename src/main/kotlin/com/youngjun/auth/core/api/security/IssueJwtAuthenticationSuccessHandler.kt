package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.core.api.controller.v1.response.LoginResponse
import com.youngjun.auth.core.api.support.response.AuthResponse
import com.youngjun.auth.core.domain.auth.Auth
import com.youngjun.auth.core.domain.token.TokenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class IssueJwtAuthenticationSuccessHandler(
    private val tokenService: TokenService,
    private val objectMapper: ObjectMapper,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val auth = SecurityContextHolder.getContext().authentication.principal as Auth
        val tokenPair = tokenService.issue(auth)
        val authResponse = AuthResponse.success(LoginResponse.from(tokenPair))
        write(response, authResponse)
    }

    private fun write(
        response: HttpServletResponse,
        body: Any,
    ) {
        response.status = HttpServletResponse.SC_OK
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}
