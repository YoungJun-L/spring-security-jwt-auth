package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.user.User
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

data class JwtAuthenticationToken private constructor(
    private val username: String,
    private val details: Map<String, Any>,
    private val authorities: Collection<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {
    init {
        setDetails(details)
        super.setAuthenticated(true)
    }

    override fun getPrincipal(): String = username

    override fun getCredentials() = null

    override fun getDetails(): Map<String, Any> = details

    companion object {
        fun authenticated(user: User): JwtAuthenticationToken =
            JwtAuthenticationToken(
                user.username,
                user.details,
                user.authorities,
            )
    }
}
