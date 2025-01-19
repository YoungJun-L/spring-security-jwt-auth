package com.youngjun.auth.core.domain.token

import io.jsonwebtoken.Jwts
import java.util.Date
import javax.crypto.SecretKey
import kotlin.time.Duration.Companion.days

data class TokenBuilder(
    val id: Long = 0,
    val userId: Long = 0,
    val refreshToken: RefreshToken = RefreshTokenBuilder().build(),
) {
    fun build(): Token =
        Token(
            id = id,
            userId = userId,
            refreshToken = refreshToken,
        )
}

data class JwtBuilder(
    val secretKey: SecretKey =
        Jwts.SIG.HS256
            .key()
            .build(),
    val subject: String = "username123",
    val issuedAt: Long = System.currentTimeMillis(),
    val expiresInMilliseconds: Long = 1.days.inWholeMilliseconds,
    val extraClaims: Map<String, Any> = emptyMap(),
) {
    fun build(): String {
        val expiration = issuedAt + expiresInMilliseconds
        return Jwts
            .builder()
            .subject(subject)
            .issuedAt(Date(issuedAt))
            .expiration(Date(expiration))
            .claims(extraClaims)
            .signWith(secretKey)
            .compact()
    }
}

data class TokenPairBuilder(
    var userId: Long = 1,
    var accessToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczNTk3MTQ5MH0.DRnoLwFpmwER9VoCmbyR-tIUIJSrRjOufzAsR3G3miA",
    var accessTokenExpiresIn: Long = 17359714909720,
    var refreshToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
    var refreshTokenExpiresIn: Long = 1738561690972,
) {
    fun build(): TokenPair =
        TokenPair(
            userId = userId,
            accessToken = accessToken,
            accessTokenExpiration = accessTokenExpiresIn,
            refreshToken = refreshToken,
            refreshTokenExpiration = refreshTokenExpiresIn,
        )
}

data class RefreshTokenBuilder(
    val value: String = "",
) {
    fun build(): RefreshToken =
        RefreshToken(
            value = value,
        )
}
