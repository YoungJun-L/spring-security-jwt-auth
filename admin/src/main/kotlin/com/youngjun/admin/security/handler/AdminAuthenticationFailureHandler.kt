package com.youngjun.admin.security.handler

import com.youngjun.admin.support.error.ErrorType
import com.youngjun.admin.support.error.ErrorType.ADMINISTRATOR_BAD_CREDENTIALS
import com.youngjun.admin.support.error.ErrorType.ADMINISTRATOR_LOCKED
import com.youngjun.admin.support.error.ErrorType.UNAUTHORIZED
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.AuthenticationFailureHandler

private const val ADMIN_EXCEPTION_LOG_FORMAT = "AdminException : {}"
private const val DEFAULT_FAILURE_URL = "/login?error"

class AdminAuthenticationFailureHandler : AuthenticationFailureHandler {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AuthenticationException,
    ) {
        val errorType = resolve(ex)
        log(ex, errorType)
        request.session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorType.message)
        redirectStrategy.sendRedirect(request, response, DEFAULT_FAILURE_URL)
    }

    private fun resolve(ex: AuthenticationException) =
        when (ex) {
            is BadCredentialsException -> ADMINISTRATOR_BAD_CREDENTIALS
            is LockedException -> ADMINISTRATOR_LOCKED
            else -> UNAUTHORIZED
        }

    private fun log(
        ex: AuthenticationException,
        errorType: ErrorType,
    ) {
        when (errorType.logLevel) {
            LogLevel.ERROR -> log.error(ADMIN_EXCEPTION_LOG_FORMAT, ex.message, ex)
            LogLevel.WARN -> log.warn(ADMIN_EXCEPTION_LOG_FORMAT, ex.message, ex)
            else -> log.info(ADMIN_EXCEPTION_LOG_FORMAT, ex.message, ex)
        }
    }
}
