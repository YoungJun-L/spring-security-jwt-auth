package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.auth.AuthService
import com.youngjun.auth.core.domain.token.TokenParser
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(
    private val tokenParser: TokenParser,
    private val authService: AuthService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        try {
            val username = tokenParser.parseSubject(authentication.principal as String)
            return createSuccessAuthentication(username)
        } catch (ex: Exception) {
            throw BadTokenException(ex.message, ex.cause)
        }
    }

    private fun createSuccessAuthentication(username: String): Authentication {
        val auth = authService.loadUserByUsername(username)
        return JwtAuthenticationToken.authenticated(auth)
    }

    override fun supports(authentication: Class<*>): Boolean = BearerTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
}
