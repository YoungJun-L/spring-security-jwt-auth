package com.youngjun.auth.domain.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Configuration
class ClockConfig {
    @Bean
    fun clock(): Clock = fixedClock
}

val fixedClock: Clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
val now: LocalDateTime = LocalDateTime.now(fixedClock)
