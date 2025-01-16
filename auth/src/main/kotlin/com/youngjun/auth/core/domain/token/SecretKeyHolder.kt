package com.youngjun.auth.core.domain.token

import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class SecretKeyHolder(
    private val secretKey: SecretKey =
        Jwts.SIG.HS256
            .key()
            .build(),
) {
    fun get(): SecretKey = secretKey
}
