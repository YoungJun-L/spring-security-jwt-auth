package com.youngjun.core.api.controller

import com.youngjun.core.support.error.CoreException
import com.youngjun.core.support.response.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
private object ApiControllerAdvice {
    private const val CORE_EXCEPTION_LOG_FORMAT = "CoreException : {}"
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CoreException::class)
    private fun handleAuthException(ex: CoreException): ResponseEntity<ApiResponse<Any>> {
        when (ex.errorType.logLevel) {
            LogLevel.ERROR -> log.error(CORE_EXCEPTION_LOG_FORMAT, ex.message, ex)
            LogLevel.WARN -> log.warn(CORE_EXCEPTION_LOG_FORMAT, ex.message, ex)
            else -> log.info(CORE_EXCEPTION_LOG_FORMAT, ex.message, ex)
        }
        return ResponseEntity
            .status(ex.errorType.status)
            .body(ApiResponse.error(ex.errorType, ex.data))
    }
}
