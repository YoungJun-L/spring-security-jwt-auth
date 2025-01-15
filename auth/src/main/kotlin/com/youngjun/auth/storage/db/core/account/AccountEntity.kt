package com.youngjun.auth.storage.db.core.account

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.account.AccountStatus
import com.youngjun.auth.storage.db.core.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "users")
@Entity
class AccountEntity(
    val username: String,
    val password: String,
    @Enumerated(EnumType.STRING)
    val status: AccountStatus = AccountStatus.ENABLED,
) : BaseEntity() {
    fun toAccount(): Account = Account(id, username, password, status)
}
