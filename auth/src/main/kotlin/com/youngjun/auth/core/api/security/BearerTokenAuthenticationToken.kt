package com.youngjun.auth.core.api.security

import org.springframework.security.authentication.AbstractAuthenticationToken

data class BearerTokenAuthenticationToken(
    private val token: String,
) : AbstractAuthenticationToken(emptyList()) {
    override fun getCredentials() = token

    override fun getPrincipal() = token
}
