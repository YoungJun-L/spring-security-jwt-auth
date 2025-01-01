package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

data class Auth(
    val id: Long,
    private val username: String,
    private val password: String,
    val status: AuthStatus,
) : UserDetails {
    override fun getUsername(): String = username

    override fun getPassword(): String = password

    override fun getAuthorities(): Collection<GrantedAuthority?> = AuthorityUtils.NO_AUTHORITIES

    override fun isAccountNonLocked(): Boolean = status != AuthStatus.LOCKED

    override fun isEnabled(): Boolean = status == AuthStatus.ENABLED

    fun verify() {
        if (!isAccountNonLocked) {
            throw AuthException(ErrorType.AUTH_LOCKED_ERROR)
        }
        if (!isEnabled) {
            throw AuthException(ErrorType.AUTH_DISABLED_ERROR)
        }
    }

    fun details(): Map<String, String> = mapOf("username" to username)
}
