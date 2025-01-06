package com.youngjun.auth.storage.db.core.auth

import com.youngjun.auth.core.domain.auth.Auth
import com.youngjun.auth.core.domain.auth.AuthStatus
import com.youngjun.auth.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "auth")
@Entity
class AuthEntity(
    val username: String,
    val password: String,
    @Enumerated(EnumType.STRING)
    val status: AuthStatus = AuthStatus.ENABLED,
) : BaseEntity() {
    fun toAuth(): Auth = Auth(id, username, password, status)
}
