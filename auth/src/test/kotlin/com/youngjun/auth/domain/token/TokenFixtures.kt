package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.hours
import com.youngjun.auth.infra.jwt.JwtBuilder
import io.jsonwebtoken.Jwts
import java.time.Duration
import java.time.LocalDateTime
import javax.crypto.SecretKey

data class RefreshTokenBuilder(
    val userId: Long = 123456789,
    val value: RawRefreshToken = RawRefreshToken(JwtBuilder(subject = userId.toString()).build()),
    val status: TokenStatus = TokenStatus.ENABLED,
) {
    fun build(): RefreshToken =
        RefreshToken(
            userId = userId,
            value = value,
            status = status,
        )
}

data class TokenPairBuilder(
    val userId: Long = 425002570,
    val accessToken: ParsedAccessToken = ParsedAccessTokenBuilder(userId = userId).build(),
    val refreshToken: ParsedRefreshToken = ParsedRefreshTokenBuilder(userId = userId).build(),
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
    val issuedAt: LocalDateTime = LocalDateTime.now(),
    val expiresIn: Duration = 12.hours,
    val secretKey: SecretKey =
        Jwts.SIG.HS256
            .key()
            .build(),
) {
    fun build(): ParsedAccessToken =
        ParsedAccessToken(
            value = RawAccessToken(JwtBuilder(userId.toString(), issuedAt, expiresIn, secretKey = secretKey).build()),
            payload = PayloadBuilder(userId, issuedAt + expiresIn).build(),
        )
}

data class ParsedRefreshTokenBuilder(
    val userId: Long = 9594670051,
    val issuedAt: LocalDateTime = LocalDateTime.now(),
    val expiresIn: Duration = 12.hours,
    val secretKey: SecretKey =
        Jwts.SIG.HS256
            .key()
            .build(),
) {
    fun build(): ParsedRefreshToken =
        ParsedRefreshToken(
            value = RawRefreshToken(JwtBuilder(userId.toString(), issuedAt, expiresIn, secretKey = secretKey).build()),
            payload = PayloadBuilder(userId, issuedAt + expiresIn).build(),
        )
}

data class PayloadBuilder(
    val userId: Long = 6208931181,
    val expiration: LocalDateTime = LocalDateTime.now(),
) {
    fun build(): Payload =
        Payload(
            userId = userId,
            expiration = expiration,
        )
}
