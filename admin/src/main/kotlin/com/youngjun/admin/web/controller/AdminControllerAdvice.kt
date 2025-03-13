package com.youngjun.admin.web.controller

import com.youngjun.admin.support.error.AdminException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.security.web.WebAttributes
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

private const val ADMIN_EXCEPTION_LOG_FORMAT = "AdminException : {}"

@ControllerAdvice
class AdminControllerAdvice {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(AdminException::class)
    private fun handleAdminException(
        ex: AdminException,
        request: HttpServletRequest,
    ): String {
        when (ex.errorType.logLevel) {
            LogLevel.ERROR -> log.error(ADMIN_EXCEPTION_LOG_FORMAT, ex.message, ex)
            LogLevel.WARN -> log.warn(ADMIN_EXCEPTION_LOG_FORMAT, ex.message, ex)
            else -> log.info(ADMIN_EXCEPTION_LOG_FORMAT, ex.message, ex)
        }
        request.session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, ex.errorType.message)
        return "redirect:${request.requestURI}?error"
    }
}
