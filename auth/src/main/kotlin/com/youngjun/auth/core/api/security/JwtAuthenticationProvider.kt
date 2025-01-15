package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.domain.token.TokenParser
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationProvider(
    private val tokenParser: TokenParser,
    private val accountService: AccountService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val username = tokenParser.parseSubject(authentication.credentials as String)
        val account = accountService.loadUserByUsername(username)
        check(account)
        return JwtAuthenticationToken.authenticated(account)
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
