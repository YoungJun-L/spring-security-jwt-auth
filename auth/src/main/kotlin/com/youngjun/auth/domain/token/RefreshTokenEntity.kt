package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "refresh_token")
@Entity
class RefreshTokenEntity(
    userId: Long,
    token: String,
    status: TokenStatus = TokenStatus.ENABLED,
) : BaseEntity() {
    @Column
    var userId = userId
        private set

    @Column
    var token = token
        private set

    @Enumerated(EnumType.STRING)
    @Column
    var status = status
        private set

    fun toRefreshToken(): RefreshToken = RefreshToken(id, userId, token, status)

    fun expire() {
        status = TokenStatus.EXPIRED
    }

    fun updateValue(value: String) {
        token = value
    }
}
