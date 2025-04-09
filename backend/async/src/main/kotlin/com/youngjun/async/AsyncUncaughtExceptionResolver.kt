package com.youngjun.async

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler

interface AsyncUncaughtExceptionResolver : AsyncUncaughtExceptionHandler {
    fun supports(ex: Throwable): Boolean
}
