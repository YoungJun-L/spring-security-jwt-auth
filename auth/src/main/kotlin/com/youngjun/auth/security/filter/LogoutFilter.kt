package com.youngjun.auth.security.filter

import com.youngjun.auth.api.controller.v1.response.LogoutResponse
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.security.handler.JsonResponseWriter
import com.youngjun.auth.support.response.AuthResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

private val logoutRequestMatcher = AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/auth/logout")

class LogoutFilter(
    private val accountService: AccountService,
    private val jsonResponseWriter: JsonResponseWriter,
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
        jsonResponseWriter.write(response, HttpStatus.OK, AuthResponse.success(LogoutResponse.from(account)))
        SecurityContextHolder.clearContext()
    }

    override fun shouldNotFilter(request: HttpServletRequest) = !logoutRequestMatcher.matches(request)
}
