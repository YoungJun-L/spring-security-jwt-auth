package com.youngjun.auth.core.domain.account

import com.youngjun.auth.core.domain.account.AccountStatus.DISABLED
import com.youngjun.auth.core.domain.account.AccountStatus.ENABLED
import com.youngjun.auth.core.domain.account.AccountStatus.LOCKED
import com.youngjun.auth.core.domain.account.AccountStatus.LOGOUT
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_LOCKED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_LOGOUT_ERROR
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

data class Account(
    val id: Long,
    private val username: String,
    private val password: String,
    val status: AccountStatus,
) : UserDetails {
    override fun getUsername(): String = username

    override fun getPassword(): String = password

    override fun getAuthorities(): Collection<GrantedAuthority> = AuthorityUtils.NO_AUTHORITIES

    override fun isAccountNonLocked(): Boolean = status != LOCKED

    override fun isEnabled(): Boolean = status == ENABLED || status == LOGOUT

    fun verify() {
        when (status) {
            LOCKED -> throw AuthException(ACCOUNT_LOCKED_ERROR)
            DISABLED -> throw AuthException(ACCOUNT_DISABLED_ERROR)
            LOGOUT -> throw AuthException(ACCOUNT_LOGOUT_ERROR)
            ENABLED -> return
        }
    }

    fun logout(): Account = Account(id, username, password, LOGOUT)
}
