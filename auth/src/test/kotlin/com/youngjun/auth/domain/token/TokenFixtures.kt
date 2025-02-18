package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.config.now
import com.youngjun.auth.domain.support.hours
import com.youngjun.auth.domain.support.toInstant
import io.jsonwebtoken.Jwts
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

data class RefreshTokenBuilder(
    val userId: Long = 123456789,
    val value: String = JwtBuilder().build(),
    val status: TokenStatus = TokenStatus.ENABLED,
) {
    fun build(): RefreshToken =
        RefreshToken(
            userId = userId,
            value = value,
            status = status,
        )
}

data class JwtBuilder(
    val subject: String = "488387734",
    val issuedAt: LocalDateTime = now,
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

data class TokenPairBuilder(
    val userId: Long = 425002570,
    val accessToken: ParsedAccessToken = ParsedAccessTokenBuilder().build(),
    val refreshToken: ParsedRefreshToken = ParsedRefreshTokenBuilder().build(),
) {
    fun build(): TokenPair =
        TokenPair(
            userId = userId,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
}

data class ParsedAccessTokenBuilder(
    val userId: Long = 4046540831,
    val issuedAt: LocalDateTime = now,
    val expiresIn: Duration = 12.hours,
    val secretKey: SecretKey =
        Jwts.SIG.HS256
            .key()
            .build(),
) {
    fun build(): ParsedAccessToken =
        ParsedAccessToken(
            value = JwtBuilder(userId.toString(), issuedAt, expiresIn, secretKey = secretKey).build(),
            payload = PayloadBuilder(userId, issuedAt + expiresIn).build(),
        )
}

data class ParsedRefreshTokenBuilder(
    val userId: Long = 9594670051,
    val issuedAt: LocalDateTime = now,
    val expiresIn: Duration = 12.hours,
    val secretKey: SecretKey =
        Jwts.SIG.HS256
            .key()
            .build(),
) {
    fun build(): ParsedRefreshToken =
        ParsedRefreshToken(
            value = JwtBuilder(userId.toString(), issuedAt, expiresIn, secretKey = secretKey).build(),
            payload = PayloadBuilder(userId, issuedAt + expiresIn).build(),
        )
}

data class PayloadBuilder(
    val userId: Long = 6208931181,
    val expiration: LocalDateTime = now,
) {
    fun build(): Payload =
        Payload(
            userId = userId,
            expiration = expiration,
        )
}
