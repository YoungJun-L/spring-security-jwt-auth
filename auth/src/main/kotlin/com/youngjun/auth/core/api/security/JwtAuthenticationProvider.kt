package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.domain.token.TokenProvider
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationProvider(
    private val tokenProvider: TokenProvider,
    private val accountService: AccountService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val username = parseUsername(authentication)
        val account = accountService.loadUserByUsername(username)
        check(account)
        return JwtAuthenticationToken.authenticated(account)
    }

    private fun parseUsername(authentication: Authentication) =
        try {
            tokenProvider.parseSubject(authentication.credentials as String)
        } catch (ex: AuthException) {
            when (ex.errorType) {
                TOKEN_EXPIRED_ERROR -> throw CredentialsExpiredException(ex.message)
                else -> throw InvalidTokenException(ex.message)
            }
        }

    private fun check(userDetails: UserDetails) {
        when {
            !userDetails.isAccountNonLocked -> throw LockedException("User account is locked")
            !userDetails.isEnabled -> throw DisabledException("User is disabled")
            !userDetails.isAccountNonExpired -> throw AccountExpiredException("User account has expired")
        }
    }

    override fun supports(authentication: Class<*>): Boolean = BearerTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
}
