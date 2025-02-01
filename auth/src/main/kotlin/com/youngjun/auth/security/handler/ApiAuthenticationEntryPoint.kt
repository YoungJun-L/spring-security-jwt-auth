package com.youngjun.auth.security.handler

import com.youngjun.auth.security.support.error.TypedAuthenticationException
import com.youngjun.auth.support.error.ErrorType
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_BAD_CREDENTIALS_ERROR
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOCKED_ERROR
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED_ERROR
import com.youngjun.auth.support.response.AuthResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class ApiAuthenticationEntryPoint(
    private val jsonResponseWriter: JsonResponseWriter,
) : AuthenticationEntryPoint {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AuthenticationException,
    ) {
        val errorType = resolve(ex)
        log(ex, errorType)
        jsonResponseWriter.write(response, errorType.status, AuthResponse.error(errorType))
    }

    private fun resolve(ex: AuthenticationException) =
        when (ex) {
            is TypedAuthenticationException -> ex.errorType
            is BadCredentialsException -> ACCOUNT_BAD_CREDENTIALS_ERROR
            is LockedException -> ACCOUNT_LOCKED_ERROR
            is DisabledException -> ACCOUNT_DISABLED_ERROR
            else -> UNAUTHORIZED_ERROR
        }

    private fun log(
        ex: AuthenticationException,
        errorType: ErrorType,
    ) {
        when (errorType.logLevel) {
            LogLevel.ERROR -> log.error("AuthException : {}", ex.message, ex)
            LogLevel.WARN -> log.warn("AuthException : {}", ex.message, ex)
            else -> log.info("AuthException : {}", ex.message, ex)
        }
    }
}
