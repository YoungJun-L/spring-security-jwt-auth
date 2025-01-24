package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.support.hours
import com.youngjun.auth.core.domain.support.toInstant
import io.jsonwebtoken.Jwts
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

data class RefreshTokenDetailsBuilder(
    val id: Long = 1,
    val userId: Long = 1,
    val refreshToken: RefreshToken = RefreshToken(JwtBuilder().build()),
    val status: TokenStatus = TokenStatus.ENABLED,
) {
    fun build(): RefreshTokenDetails =
        RefreshTokenDetails(
            id = id,
            userId = userId,
            refreshToken = refreshToken,
            status = status,
        )
}

data class NewRefreshTokenBuilder(
    val userId: Long = 1,
    val refreshToken: RefreshToken = RefreshToken(JwtBuilder().build()),
) {
    fun build(): NewRefreshToken =
        NewRefreshToken(
            userId = userId,
            refreshToken = refreshToken,
        )
}

data class JwtBuilder(
    val secretKey: SecretKey =
        Jwts.SIG.HS256
            .key()
            .build(),
    val subject: String = "1",
    val issuedAt: LocalDateTime = LocalDateTime.now(),
    val expiresIn: Duration = 12.hours,
    val extraClaims: Map<String, Any> = emptyMap(),
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

data class TokenPairDetailsBuilder(
    val userId: Long = 1,
    val accessToken: AccessToken = AccessToken(JwtBuilder().build()),
    val accessTokenExpiration: LocalDateTime = LocalDateTime.now() + 1.hours,
    val refreshToken: RefreshToken = RefreshToken(JwtBuilder().build()),
    val refreshTokenExpiration: LocalDateTime = LocalDateTime.now() + 12.hours,
) {
    fun build(): TokenPairDetails =
        TokenPairDetails(
            userId = userId,
            accessToken = accessToken,
            accessTokenExpiration = accessTokenExpiration,
            refreshToken = refreshToken,
            refreshTokenExpiration = refreshTokenExpiration,
        )
}
