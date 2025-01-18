package com.youngjun.auth.core.domain.account

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_LOCKED_ERROR
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

data class Account(
    val id: Long,
    private val username: String,
    private val password: String,
    private val status: AccountStatus,
    val details: Map<String, Any> = mapOf("username" to username),
) : UserDetails {
    override fun getUsername(): String = username

    override fun getPassword(): String = password

    override fun getAuthorities(): Collection<GrantedAuthority> = AuthorityUtils.NO_AUTHORITIES

    override fun isAccountNonLocked(): Boolean = status != AccountStatus.LOCKED

    override fun isEnabled(): Boolean = status == AccountStatus.ENABLED

    fun verify() {
        if (!isAccountNonLocked) {
            throw AuthException(ACCOUNT_LOCKED_ERROR)
        }
        if (!isEnabled) {
            throw AuthException(ACCOUNT_DISABLED_ERROR)
        }
    }
}
