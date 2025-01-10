package com.youngjun.auth.core.domain.user

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.USER_DISABLED_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.USER_LOCKED_ERROR
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

data class User(
    val id: Long,
    private val username: String,
    private val password: String,
    private val status: UserStatus,
    val details: Map<String, Any> = emptyMap(),
) : UserDetails {
    override fun getUsername(): String = username

    override fun getPassword(): String = password

    override fun getAuthorities(): Collection<GrantedAuthority> = AuthorityUtils.NO_AUTHORITIES

    override fun isAccountNonLocked(): Boolean = status != UserStatus.LOCKED

    override fun isEnabled(): Boolean = status == UserStatus.ENABLED

    fun verify() {
        if (!isAccountNonLocked) {
            throw AuthException(USER_LOCKED_ERROR)
        }
        if (!isEnabled) {
            throw AuthException(USER_DISABLED_ERROR)
        }
    }
}
