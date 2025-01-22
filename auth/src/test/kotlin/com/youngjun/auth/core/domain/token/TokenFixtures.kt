package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.support.hours
import com.youngjun.auth.core.domain.support.toInstant
import io.jsonwebtoken.Jwts
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

data class TokenBuilder(
    val id: Long = 1,
    val userId: Long = 1,
    val refreshToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
    val status: TokenStatus = TokenStatus.ENABLED,
) {
    fun build(): Token =
        Token(
            id = id,
            userId = userId,
            refreshToken = refreshToken,
            status = status,
        )
}

data class NewTokenBuilder(
    val userId: Long = 1,
    val refreshToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
) {
    fun build(): NewToken =
        NewToken(
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

data class TokenPairBuilder(
    var userId: Long = 1,
    var accessToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczNTk3MTQ5MH0.DRnoLwFpmwER9VoCmbyR-tIUIJSrRjOufzAsR3G3miA",
    var accessTokenExpiresIn: LocalDateTime = LocalDateTime.now(),
    var refreshToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
    var refreshTokenExpiresIn: LocalDateTime = LocalDateTime.now(),
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
