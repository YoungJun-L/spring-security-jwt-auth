package com.youngjun.auth.api.controller

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.BAD_REQUEST
import com.youngjun.auth.support.error.ErrorType.DEFAULT
import com.youngjun.auth.support.error.ErrorType.NOT_FOUND
import com.youngjun.auth.support.response.AuthResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

private const val AUTH_EXCEPTION_LOG_FORMAT = "AuthException : {}"

@RestControllerAdvice
private object AuthControllerAdvice {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(AuthException::class)
    private fun handleAuthException(ex: AuthException): ResponseEntity<AuthResponse<Any>> {
        when (ex.errorType.logLevel) {
            LogLevel.ERROR -> log.error(AUTH_EXCEPTION_LOG_FORMAT, ex.message, ex)
            LogLevel.WARN -> log.warn(AUTH_EXCEPTION_LOG_FORMAT, ex.message, ex)
            else -> log.info(AUTH_EXCEPTION_LOG_FORMAT, ex.message, ex)
        }
        return ResponseEntity
            .status(ex.errorType.status)
            .body(AuthResponse.error(ex.errorType, ex.data))
    }

    @ExceptionHandler(
        IllegalArgumentException::class,
        HttpRequestMethodNotSupportedException::class,
        MissingServletRequestParameterException::class,
        MissingServletRequestPartException::class,
        MethodArgumentTypeMismatchException::class,
        HttpMessageNotReadableException::class,
        HttpMediaTypeNotSupportedException::class,
        HttpMediaTypeNotAcceptableException::class,
        HandlerMethodValidationException::class,
    )
    private fun handleBadRequest(ex: Exception): ResponseEntity<AuthResponse<Any>> {
        log.info("Bad Request: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthResponse.error(BAD_REQUEST))
    }

    @ExceptionHandler(NoHandlerFoundException::class, NoResourceFoundException::class)
    private fun handleNotFoundException(ex: Exception): ResponseEntity<AuthResponse<Any>> {
        log.info("Not Found: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AuthResponse.error(NOT_FOUND))
    }

    @ExceptionHandler(Exception::class)
    private fun handleException(ex: Exception): ResponseEntity<AuthResponse<Any>> {
        log.info("Exception: {}", ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(AuthResponse.error(DEFAULT))
    }
}
