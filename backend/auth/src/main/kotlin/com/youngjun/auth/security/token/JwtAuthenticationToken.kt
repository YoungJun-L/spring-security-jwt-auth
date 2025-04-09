package com.youngjun.auth.security.token

import com.youngjun.auth.domain.account.Account
import org.springframework.security.authentication.AbstractAuthenticationToken

data class JwtAuthenticationToken private constructor(
    private val account: Account,
) : AbstractAuthenticationToken(account.authorities) {
    init {
        super.setAuthenticated(true)
    }

    override fun getPrincipal(): Account = account

    override fun getCredentials() = null

    companion object {
        fun authenticated(account: Account): JwtAuthenticationToken = JwtAuthenticationToken(account)
    }
}
