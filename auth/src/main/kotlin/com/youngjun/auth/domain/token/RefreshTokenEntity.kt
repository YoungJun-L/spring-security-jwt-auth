package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "refresh_token")
@Entity
class RefreshTokenEntity(
    val userId: Long,
    var token: String,
    @Enumerated(EnumType.STRING)
    var status: TokenStatus = TokenStatus.ENABLED,
) : BaseEntity() {
    fun toRefreshToken(): RefreshToken = RefreshToken(id, userId, token, status)
}
