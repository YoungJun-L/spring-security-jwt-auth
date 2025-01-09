package com.youngjun.auth.core.domain.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneId
import java.util.Date
import kotlin.time.Duration.Companion.days

data class JwtBuilder(
    val secretKey: String,
    val subject: String = "username123",
    val issuedAt: LocalDateTime = now(),
    val expiresInSeconds: Long = 30.days.inWholeSeconds,
    val extraClaims: Map<String, Any> = emptyMap(),
) {
    fun build(): String {
        val expiration = issuedAt.atZone(ZoneId.systemDefault()).toEpochSecond() + expiresInSeconds
        return Jwts
            .builder()
            .subject(subject)
            .issuedAt(Date(issuedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
            .expiration(Date(expiration * 1_000))
            .claims(extraClaims)
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
    }
}

data class TokenPairBuilder(
    var authId: Long = 1,
    var accessToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczNTk3MTQ5MH0.DRnoLwFpmwER9VoCmbyR-tIUIJSrRjOufzAsR3G3miA",
    var accessTokenExpiresIn: Long = 17359714909720,
    var refreshToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
    var refreshTokenExpiresIn: Long = 1738561690972,
) {
    fun build(): TokenPair =
        TokenPair(
            authId = authId,
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
