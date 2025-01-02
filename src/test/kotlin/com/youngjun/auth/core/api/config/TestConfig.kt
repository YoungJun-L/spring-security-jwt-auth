package com.youngjun.auth.core.api.config

import com.youngjun.auth.core.domain.support.TimeHolder
import com.youngjun.auth.core.domain.token.TokenParser
import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant.now

@Configuration
class TestConfig {
    private val now = now().toEpochMilli()

    @Bean
    fun tokenParser(): TokenParser = mockk()

    @Bean
    fun timeHolder() = TimeHolder { now }
}
