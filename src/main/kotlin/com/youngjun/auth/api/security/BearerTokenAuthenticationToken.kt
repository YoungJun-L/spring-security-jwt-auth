package com.youngjun.auth.api.security

import org.springframework.security.authentication.AbstractAuthenticationToken

data class BearerTokenAuthenticationToken(
    private val token: String,
) : AbstractAuthenticationToken(emptyList()) {
    override fun getCredentials() = token

    override fun getPrincipal() = token
}
