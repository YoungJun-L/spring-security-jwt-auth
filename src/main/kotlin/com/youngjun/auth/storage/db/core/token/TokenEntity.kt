package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.Token
import com.youngjun.auth.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "token")
@Entity
class TokenEntity(
    val authId: Long,
    val refreshToken: String,
) : BaseEntity() {
    fun toToken(): Token = Token(authId, RefreshToken(refreshToken))
}
