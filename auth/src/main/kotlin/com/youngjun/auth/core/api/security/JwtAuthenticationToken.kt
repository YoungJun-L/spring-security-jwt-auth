package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.account.Account
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

data class JwtAuthenticationToken private constructor(
    private val id: Long,
    private val authorities: Collection<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {
    init {
        super.setAuthenticated(true)
    }

    override fun getPrincipal(): Long = id

    override fun getCredentials() = null

    companion object {
        fun authenticated(account: Account): JwtAuthenticationToken =
            JwtAuthenticationToken(
                account.id,
                account.authorities,
            )
    }
}
