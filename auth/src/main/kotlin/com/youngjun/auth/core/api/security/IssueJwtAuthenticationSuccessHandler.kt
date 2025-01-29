package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.controller.v1.response.LoginResponse
import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.support.response.AuthResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class IssueJwtAuthenticationSuccessHandler(
    private val tokenService: TokenService,
    private val objectMapper: ObjectMapper,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val tokenPair = tokenService.issue((authentication.principal as Account).id)
        val authResponse = AuthResponse.success(LoginResponse.from(tokenPair))
        write(response, authResponse)
    }

    private fun write(
        response: HttpServletResponse,
        body: Any,
    ) {
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(body))
    }
}
