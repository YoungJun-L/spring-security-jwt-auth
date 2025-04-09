package com.youngjun.auth.security.provider

import com.youngjun.auth.application.TokenService
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.security.support.error.TypedAuthenticationException
import com.youngjun.auth.security.token.BearerTokenAuthenticationToken
import com.youngjun.auth.security.token.JwtAuthenticationToken
import com.youngjun.auth.support.error.AuthException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

class JwtAuthenticationProvider(
    private val tokenService: TokenService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication =
        try {
            JwtAuthenticationToken.authenticated(tokenService.parse(RawAccessToken(authentication.credentials as String)))
        } catch (ex: AuthException) {
            throw TypedAuthenticationException(ex.errorType)
        }

    override fun supports(authentication: Class<*>): Boolean = BearerTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
}
