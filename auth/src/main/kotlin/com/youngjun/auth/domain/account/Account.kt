package com.youngjun.auth.domain.account

import com.youngjun.auth.domain.account.AccountStatus.DISABLED
import com.youngjun.auth.domain.account.AccountStatus.ENABLED
import com.youngjun.auth.domain.account.AccountStatus.LOCKED
import com.youngjun.auth.domain.account.AccountStatus.LOGOUT
import com.youngjun.auth.domain.support.BaseEntity
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOCKED_ERROR
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOGOUT_ERROR
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

@Table(name = "users")
@Entity
class Account(
    username: String,
    password: String,
    status: AccountStatus = ENABLED,
) : BaseEntity(),
    UserDetails {
    @Column
    private var username = username

    @Column
    private var password = password

    @Enumerated(EnumType.STRING)
    @Column
    var status = status
        private set

    override fun getUsername(): String = username

    override fun getPassword(): String = password

    override fun getAuthorities(): Collection<GrantedAuthority> = AuthorityUtils.NO_AUTHORITIES

    override fun isAccountNonLocked(): Boolean = status != LOCKED

    override fun isEnabled(): Boolean = status in arrayOf(ENABLED, LOGOUT)

    fun verify() {
        when (status) {
            LOCKED -> throw AuthException(ACCOUNT_LOCKED_ERROR)
            DISABLED -> throw AuthException(ACCOUNT_DISABLED_ERROR)
            LOGOUT -> throw AuthException(ACCOUNT_LOGOUT_ERROR)
            ENABLED -> return
        }
    }

    fun enable() {
        status = ENABLED
    }

    fun logout() {
        status = LOGOUT
    }
}
