package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.api.controller.v1.response.LogoutResponse
import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.support.response.AuthResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

private val logoutRequestMatcher = AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/auth/logout")

class LogoutFilter(
    private val accountService: AccountService,
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
        val account = accountService.logout(authentication.principal as Account)
        SecurityContextHolder.clearContext()
        write(response, AuthResponse.success(LogoutResponse.from(account)))
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

    override fun shouldNotFilter(request: HttpServletRequest) = !logoutRequestMatcher.matches(request)
}
