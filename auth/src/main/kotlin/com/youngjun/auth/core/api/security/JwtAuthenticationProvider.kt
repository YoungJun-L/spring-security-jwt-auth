package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.account.AccountReader
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_LOCKED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JwtAuthenticationProvider(
    private val tokenParser: TokenParser,
    private val accountReader: AccountReader,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val userId = parseUserId(authentication)
        val account = readEnabled(userId)
        return JwtAuthenticationToken.authenticated(account)
    }

    private fun parseUserId(authentication: Authentication) =
        try {
            tokenParser.parseUserId(authentication.credentials as String)
        } catch (ex: AuthException) {
            when (ex.errorType) {
                TOKEN_EXPIRED_ERROR -> throw CredentialsExpiredException(ex.message)
                else -> throw InvalidTokenException(ex.message)
            }
        }

    private fun readEnabled(userId: Long) =
        try {
            accountReader.readEnabled(userId)
        } catch (ex: AuthException) {
            when (ex.errorType) {
                ACCOUNT_LOCKED_ERROR -> throw LockedException("User account is locked")
                ACCOUNT_DISABLED_ERROR -> throw DisabledException("User is disabled")
                else -> throw UsernameNotFoundException("Cannot find user")
            }
        }

    override fun supports(authentication: Class<*>): Boolean = BearerTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
}
