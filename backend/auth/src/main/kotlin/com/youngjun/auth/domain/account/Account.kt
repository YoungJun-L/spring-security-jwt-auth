package com.youngjun.auth.domain.account

import com.youngjun.auth.domain.account.AccountStatus.DISABLED
import com.youngjun.auth.domain.account.AccountStatus.ENABLED
import com.youngjun.auth.domain.account.AccountStatus.LOCKED
import com.youngjun.auth.domain.account.AccountStatus.LOGOUT
import com.youngjun.auth.domain.support.BaseEntity
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOCKED
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOGOUT
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder

@Table(name = "users")
@Entity
class Account(
    @Column(name = "email")
    val emailAddress: EmailAddress,
    @Column(name = "password")
    private var password: Password,
    profile: Profile,
    status: AccountStatus = ENABLED,
) : BaseEntity(),
    UserDetails {
    @Embedded
    var profile: Profile = profile
        protected set

    @Enumerated(EnumType.STRING)
    @Column
    var status = status
        protected set

    override fun getUsername(): String = emailAddress.value

    override fun getPassword(): String = password.value

    override fun getAuthorities(): Collection<GrantedAuthority> = AuthorityUtils.NO_AUTHORITIES

    override fun isAccountNonLocked(): Boolean = status != LOCKED

    override fun isEnabled(): Boolean = status in arrayOf(ENABLED, LOGOUT)

    fun verify() {
        when (status) {
            LOCKED -> throw AuthException(ACCOUNT_LOCKED)
            DISABLED -> throw AuthException(ACCOUNT_DISABLED)
            LOGOUT -> throw AuthException(ACCOUNT_LOGOUT)
            ENABLED -> return
        }
    }

    fun checkNotDisabled() {
        if (status == DISABLED) {
            throw AuthException(ACCOUNT_DISABLED)
        }
    }

    fun verify(
        rawPassword: RawPassword,
        passwordEncoder: PasswordEncoder,
    ) {
        password.verify(rawPassword, passwordEncoder)
    }

    fun enable() {
        status = ENABLED
    }

    fun logout() {
        status = LOGOUT
    }

    fun changePassword(
        newPassword: RawPassword,
        passwordEncoder: PasswordEncoder,
    ) {
        password = Password.encodedWith(newPassword, passwordEncoder)
    }
}
