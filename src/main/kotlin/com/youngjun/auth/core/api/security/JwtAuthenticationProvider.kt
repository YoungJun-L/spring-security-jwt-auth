package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.application.UserService
import com.youngjun.auth.core.domain.token.TokenParser
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationProvider(
    private val tokenParser: TokenParser,
    private val userService: UserService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val username = tokenParser.parseSubject(authentication.credentials as String)
        val user = userService.loadUserByUsername(username)
        check(user)
        return JwtAuthenticationToken.authenticated(user)
    }

    private fun check(user: UserDetails) {
        when {
            !user.isAccountNonLocked -> throw LockedException("User account is locked")
            !user.isEnabled -> throw DisabledException("User is disabled")
            !user.isAccountNonExpired -> throw AccountExpiredException("User account has expired")
        }
    }

    override fun supports(authentication: Class<*>): Boolean = BearerTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
}
