package com.youngjun.core.api.config

import com.youngjun.async.AsyncUncaughtExceptionResolver
import com.youngjun.core.support.error.CoreException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.stereotype.Component
import java.lang.reflect.Method

private const val CORE_EXCEPTION_LOG_FORMAT = "CoreException : {}"

@Component
object AsyncUncaughtCoreExceptionResolver : AsyncUncaughtExceptionResolver {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun supports(ex: Throwable): Boolean = ex is CoreException

    override fun handleUncaughtException(
        ex: Throwable,
        method: Method,
        vararg params: Any,
    ) {
        when ((ex as CoreException).errorType.logLevel) {
            LogLevel.ERROR -> log.error(CORE_EXCEPTION_LOG_FORMAT, ex.message, ex)
            LogLevel.WARN -> log.warn(CORE_EXCEPTION_LOG_FORMAT, ex.message, ex)
            else -> log.info(CORE_EXCEPTION_LOG_FORMAT, ex.message, ex)
        }
    }
}
