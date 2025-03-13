package com.youngjun.admin.domain.administrator

import com.youngjun.admin.domain.administrator.AdministratorStatus.ENABLED
import com.youngjun.admin.domain.administrator.AdministratorStatus.PENDING
import com.youngjun.admin.domain.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Table(name = "administrator")
@Entity
class Administrator(
    @Column(name = "email")
    val emailAddress: EmailAddress,
    @Column(name = "password")
    private var password: Password,
    @Column
    val name: String,
) : BaseEntity(),
    UserDetails {
    @Enumerated(EnumType.STRING)
    @Column
    var status = PENDING
        protected set

    override fun getUsername(): String = emailAddress.value

    override fun getPassword(): String = password.value

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_ADMIN"))

    override fun isAccountNonLocked(): Boolean = status == ENABLED

    fun approve() {
        status = ENABLED
    }
}
