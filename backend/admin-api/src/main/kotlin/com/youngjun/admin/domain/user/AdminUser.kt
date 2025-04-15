package com.youngjun.admin.domain.user

import com.youngjun.admin.domain.support.BaseEntity
import com.youngjun.admin.domain.user.UserStatus.ENABLED
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "users")
@Entity
class AdminUser(
    @Column(name = "email")
    val emailAddress: EmailAddress,
    profile: AdminProfile,
    status: UserStatus = ENABLED,
) : BaseEntity() {
    @Embedded
    var profile: AdminProfile = profile
        protected set

    @Enumerated(EnumType.STRING)
    @Column
    var status = status
        protected set
}
