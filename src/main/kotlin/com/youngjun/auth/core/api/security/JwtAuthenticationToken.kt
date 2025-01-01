package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.auth.Auth
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

data class JwtAuthenticationToken private constructor(
    private val userId: Long,
    private val details: Map<String, String>,
    private val authorities: Collection<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {
    init {
        setDetails(details)
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any? = null

    override fun getPrincipal(): Any = userId

    companion object {
        fun authenticated(auth: Auth): JwtAuthenticationToken = JwtAuthenticationToken(auth.id, auth.details(), auth.authorities)
    }
}
