package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenStatus
import com.youngjun.auth.storage.db.core.support.BaseEntity
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
    val status: TokenStatus = TokenStatus.ENABLED,
) : BaseEntity() {
    fun toRefreshToken(): RefreshToken = RefreshToken(id, userId, token, status)
}
