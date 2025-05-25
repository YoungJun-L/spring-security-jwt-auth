package com.youngjun.admin.infra.mail

import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestMailConfig {
    @Bean
    fun adminMailSender(): AdminMailSender = mockk(relaxed = true)
}
