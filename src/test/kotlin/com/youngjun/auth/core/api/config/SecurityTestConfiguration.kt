package com.youngjun.auth.core.api.config

import com.youngjun.auth.core.domain.token.TokenParser
import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityTestConfiguration {
    @Bean
    fun tokenParser(): TokenParser = mockk()
}
