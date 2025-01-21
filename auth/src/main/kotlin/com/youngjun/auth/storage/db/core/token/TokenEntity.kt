package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.Token
import com.youngjun.auth.core.domain.token.TokenStatus
import com.youngjun.auth.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "token")
@Entity
class TokenEntity(
    val userId: Long,
    var refreshToken: String,
    @Enumerated(EnumType.STRING)
    val status: TokenStatus = TokenStatus.ENABLED,
) : BaseEntity() {
    fun toToken(): Token = Token(id, userId, refreshToken, status)
}
