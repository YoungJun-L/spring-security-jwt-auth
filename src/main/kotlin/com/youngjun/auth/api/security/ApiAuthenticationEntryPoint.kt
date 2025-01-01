package com.youngjun.auth.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.api.support.error.ErrorType
import com.youngjun.auth.api.support.response.AuthResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class ApiAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AuthenticationException,
    ) {
        val errorType = resolve(ex)
        log(ex, errorType)
        write(response, errorType)
    }

    private fun resolve(ex: AuthenticationException) =
        when (ex) {
            is BadTokenException -> {
                ErrorType.TOKEN_INVALID_ERROR
            }

            is BadCredentialsException -> {
                ErrorType.AUTH_BAD_CREDENTIALS_ERROR
            }

            else -> {
                ErrorType.UNAUTHORIZED_ERROR
            }
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

    private fun write(
        response: HttpServletResponse,
        errorType: ErrorType,
    ) {
        response.status = errorType.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(AuthResponse.error(errorType)))
    }
}
