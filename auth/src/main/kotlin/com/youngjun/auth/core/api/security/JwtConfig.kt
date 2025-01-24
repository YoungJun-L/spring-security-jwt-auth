package com.youngjun.auth.core.api.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.jwt")
class JwtConfig(
    val accessSecretKey: String,
    val refreshSecretKey: String,
)
