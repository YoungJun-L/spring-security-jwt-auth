package com.youngjun.auth.security.config

import com.youngjun.auth.domain.support.hours
import java.time.Duration

data class JwtPropertiesBuilder(
    val accessSecretKey: String = "5ZCno4gJ5QAnL7s6h5vbyWNMWz/RbmGpOXPooCZUI2g=",
    val refreshSecretKey: String = "H1lYB7BuPya5f3XfKBQQ0WdQ03CYjNJJMMZETFqf6w8=",
    val accessTokenExpiresIn: Duration = 1.hours,
    val refreshTokenExpiresIn: Duration = 12.hours,
    val expirationThreshold: Duration = 1.hours,
) {
    fun build(): JwtProperties =
        JwtProperties(
            accessSecretKey = accessSecretKey,
            refreshSecretKey = refreshSecretKey,
            accessTokenExpiresIn = accessTokenExpiresIn,
            refreshTokenExpiresIn = refreshTokenExpiresIn,
            expirationThreshold = expirationThreshold,
        )
}
