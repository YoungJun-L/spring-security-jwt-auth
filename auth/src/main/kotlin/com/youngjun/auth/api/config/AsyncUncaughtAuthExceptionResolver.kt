package com.youngjun.auth.api.config

import com.youngjun.async.AsyncUncaughtExceptionResolver
import com.youngjun.auth.support.error.AuthException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
object AsyncUncaughtAuthExceptionResolver : AsyncUncaughtExceptionResolver {
    private const val AUTH_EXCEPTION_LOG_FORMAT = "AuthException : {}"
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun supports(ex: Throwable): Boolean = ex is AuthException

    override fun handleUncaughtException(
        ex: Throwable,
        method: Method,
        vararg params: Any,
    ) {
        when ((ex as AuthException).errorType.logLevel) {
            LogLevel.ERROR -> log.error(AUTH_EXCEPTION_LOG_FORMAT, ex.message, ex)
            LogLevel.WARN -> log.warn(AUTH_EXCEPTION_LOG_FORMAT, ex.message, ex)
            else -> log.info(AUTH_EXCEPTION_LOG_FORMAT, ex.message, ex)
        }
    }
}
