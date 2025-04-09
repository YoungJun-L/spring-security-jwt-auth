package com.youngjun.admin.domain.user

import com.youngjun.admin.domain.support.BaseEntity
import com.youngjun.admin.domain.user.UserStatus.ENABLED
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "users")
@Entity
class User(
    @Column(name = "email")
    val emailAddress: EmailAddress,
    status: UserStatus = ENABLED,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    @Column
    var status = status
        protected set
}
