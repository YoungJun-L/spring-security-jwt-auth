package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.BaseEntity
import com.youngjun.auth.domain.token.TokenStatus.ENABLED
import com.youngjun.auth.domain.token.TokenStatus.EXPIRED
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "refresh_token")
@Entity
class RefreshToken(
    userId: Long,
    value: String,
    status: TokenStatus = ENABLED,
) : BaseEntity() {
    @Column
    var userId = userId
        private set

    @Column(name = "token")
    var value = value
        private set

    @Enumerated(EnumType.STRING)
    @Column
    var status = status
        private set

    fun verify() {
        when (status) {
            EXPIRED -> throw AuthException(TOKEN_EXPIRED)
            ENABLED -> return
        }
    }

    fun expire() {
        status = EXPIRED
    }
}
