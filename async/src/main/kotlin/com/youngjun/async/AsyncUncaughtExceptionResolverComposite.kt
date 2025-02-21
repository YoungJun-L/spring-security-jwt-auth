package com.youngjun.async

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method

class AsyncUncaughtExceptionResolverComposite(
    private val asyncUncaughtExceptionResolvers: List<AsyncUncaughtExceptionResolver>,
) : AsyncUncaughtExceptionHandler {
    override fun handleUncaughtException(
        ex: Throwable,
        method: Method,
        vararg params: Any,
    ) = (asyncUncaughtExceptionResolvers.firstOrNull { it.supports(ex) } ?: DefaultAsyncUncaughtExceptionResolver)
        .handleUncaughtException(ex, method, params)
}
