package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.controller.v1.response.LoginResponse
import com.youngjun.auth.core.api.support.response.AuthResponse
import com.youngjun.auth.core.domain.user.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.nio.charset.StandardCharsets

class IssueJwtAuthenticationSuccessHandler(
    private val tokenService: TokenService,
    private val objectMapper: ObjectMapper,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val tokenPair = tokenService.issue(authentication.principal as User)
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
