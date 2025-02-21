package com.youngjun.async

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

object DefaultAsyncUncaughtExceptionResolver : AsyncUncaughtExceptionResolver {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun supports(ex: Throwable): Boolean = true

    override fun handleUncaughtException(
        ex: Throwable,
        method: Method,
        vararg params: Any,
    ) = log.error("Exception: {}", ex.message, ex)
}
