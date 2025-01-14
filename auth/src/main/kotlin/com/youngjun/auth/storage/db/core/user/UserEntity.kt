package com.youngjun.auth.storage.db.core.user

import com.youngjun.auth.core.domain.user.User
import com.youngjun.auth.core.domain.user.UserStatus
import com.youngjun.auth.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "users")
@Entity
class UserEntity(
    val username: String,
    val password: String,
    @Enumerated(EnumType.STRING)
    val status: UserStatus = UserStatus.ENABLED,
) : BaseEntity() {
    fun toUser(): User = User(id, username, password, status)
}
