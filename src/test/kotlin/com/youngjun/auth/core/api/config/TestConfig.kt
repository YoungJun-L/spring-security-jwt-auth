package com.youngjun.auth.core.api.config

import com.youngjun.auth.core.api.security.StubPasswordEncoder
import com.youngjun.auth.core.domain.token.TokenParser
import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class TestConfig {
    @Bean
    fun tokenParser(): TokenParser = mockk()

    @Bean
    fun passwordEncoder(): PasswordEncoder = StubPasswordEncoder()
}
