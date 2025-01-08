package com.youngjun.auth.core.domain.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneId
import java.util.Date
import kotlin.time.Duration.Companion.days

fun buildJwt(
    secretKey: String,
    subject: String = "username123",
    issuedAt: LocalDateTime = now(),
    expiresInSeconds: Long = 30.days.inWholeSeconds,
    extraClaims: Map<String, Any> = emptyMap(),
): String {
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
