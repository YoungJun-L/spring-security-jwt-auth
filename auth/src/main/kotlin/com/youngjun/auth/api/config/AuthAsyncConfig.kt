package com.youngjun.auth.api.config

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@ConditionalOnMissingBean(AsyncConfigurer::class)
@EnableAsync
@Configuration
private class AuthAsyncConfig : AsyncConfigurer {
    override fun getAsyncExecutor(): Executor =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 10
            maxPoolSize = 10
            queueCapacity = 10000
            setWaitForTasksToCompleteOnShutdown(true)
            setAwaitTerminationSeconds(10)
            initialize()
        }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler = AsyncUncaughtAuthExceptionResolver
}
