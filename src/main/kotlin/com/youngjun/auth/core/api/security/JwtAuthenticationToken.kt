package com.youngjun.auth.core.api.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class JwtAuthenticationToken private constructor(
    private val username: String,
    private val details: Map<String, String>,
    private val authorities: Collection<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {
    init {
        setDetails(details)
        super.setAuthenticated(true)
    }

    override fun getCredentials() = null

    override fun getPrincipal(): Any = username

    // TODO: Auth: UserDetails -> Token: Authentication(val details)
    companion object {
        fun authenticated(userDetails: UserDetails): JwtAuthenticationToken =
            JwtAuthenticationToken(userDetails.username, emptyMap(), userDetails.authorities)
    }
}
