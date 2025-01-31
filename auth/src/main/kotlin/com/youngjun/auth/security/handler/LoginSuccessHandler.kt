package com.youngjun.auth.security.handler

import com.youngjun.auth.api.controller.v1.response.LoginResponse
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.application.TokenService
import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.support.response.AuthResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class LoginSuccessHandler(
    private val accountService: AccountService,
    private val tokenService: TokenService,
    private val jsonResponseWriter: JsonResponseWriter,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val account = authentication.principal as Account
        accountService.login(account)
        jsonResponseWriter.write(
            response,
            HttpStatus.OK,
            AuthResponse.success(LoginResponse.from(tokenService.issue(account.id))),
        )
        SecurityContextHolder.clearContext()
    }
}
