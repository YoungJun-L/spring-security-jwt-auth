package com.youngjun.core.api.controller

import com.youngjun.core.support.error.CoreException
import com.youngjun.core.support.error.ErrorType.DEFAULT_ERROR
import com.youngjun.core.support.response.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CoreException::class)
    fun handleAuthException(ex: CoreException): ResponseEntity<ApiResponse<Any>> {
        when (ex.errorType.logLevel) {
            LogLevel.ERROR -> log.error("CoreException : {}", ex.message, ex)
            LogLevel.WARN -> log.warn("CoreException : {}", ex.message, ex)
            else -> log.info("CoreException : {}", ex.message, ex)
        }
        return ResponseEntity
            .status(ex.errorType.status)
            .body(ApiResponse.error(ex.errorType, ex.data))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
        log.info("Exception: {}", ex.message, ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(DEFAULT_ERROR))
    }
}
