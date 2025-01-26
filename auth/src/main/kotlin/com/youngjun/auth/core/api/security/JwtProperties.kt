package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.support.hours
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import javax.crypto.SecretKey

@ConfigurationProperties(prefix = "security.jwt")
class JwtProperties(
    accessSecretKey: String,
    refreshSecretKey: String,
    val accessTokenExpiresIn: Duration = 1.hours,
    val refreshTokenExpiresIn: Duration = 12.hours,
    val expirationThreshold: Duration = 1.hours,
) {
    val accessSecretKey: SecretKey = Keys.hmacShaKeyFor(accessSecretKey.toByteArray())
    val refreshSecretKey: SecretKey = Keys.hmacShaKeyFor(refreshSecretKey.toByteArray())
}
