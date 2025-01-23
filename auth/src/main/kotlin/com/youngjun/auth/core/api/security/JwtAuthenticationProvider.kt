package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.domain.token.AccessToken
import com.youngjun.auth.core.support.error.AuthException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

class JwtAuthenticationProvider(
    private val tokenService: TokenService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication =
        try {
            JwtAuthenticationToken.authenticated(tokenService.parse(AccessToken(authentication.credentials as String)))
        } catch (ex: AuthException) {
            throw TypedAuthenticationException(ex.errorType)
        }

    override fun supports(authentication: Class<*>): Boolean = BearerTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
}
