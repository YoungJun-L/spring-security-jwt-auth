package com.youngjun.auth.infra.jwt

import com.youngjun.auth.domain.support.hours
import com.youngjun.auth.domain.support.toInstant
import io.jsonwebtoken.Jwts
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

data class JwtBuilder(
    val subject: String = "488387734",
    val issuedAt: LocalDateTime = LocalDateTime.now(),
    val expiresIn: Duration = 12.hours,
    val extraClaims: Map<String, Any> = emptyMap(),
    val secretKey: SecretKey =
        Jwts.SIG.HS256
            .key()
            .build(),
) {
    fun build(): String =
        Jwts
            .builder()
            .subject(subject)
            .issuedAt(Date.from(issuedAt.toInstant()))
            .expiration(Date.from((issuedAt + expiresIn).toInstant()))
            .claims(extraClaims)
            .signWith(secretKey)
            .compact()
}
