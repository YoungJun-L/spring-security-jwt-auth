package com.youngjun.auth.domain.account

import com.youngjun.auth.domain.support.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "users")
@Entity
class AccountEntity(
    private var username: String,
    private var password: String,
    @Enumerated(EnumType.STRING)
    var status: AccountStatus = AccountStatus.ENABLED,
) : BaseEntity() {
    fun toAccount(): Account = Account(id, username, password, status)

    fun update(account: Account) {
        username = account.username
        password = account.password
        status = account.status
    }
}
