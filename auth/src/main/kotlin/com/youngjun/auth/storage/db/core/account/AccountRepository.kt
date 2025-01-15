package com.youngjun.auth.storage.db.core.account

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.account.NewAccount
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class AccountRepository(
    private val accountJpaRepository: AccountJpaRepository,
) {
    fun write(newAccount: NewAccount): Account {
        val savedAccount = accountJpaRepository.save(AccountEntity(newAccount.username, newAccount.password))
        return savedAccount.toAccount()
    }

    fun read(username: String): Account? = accountJpaRepository.findByUsername(username)?.toAccount()

    fun read(id: Long): Account? = accountJpaRepository.findByIdOrNull(id)?.toAccount()

    fun existsByUsername(username: String): Boolean = accountJpaRepository.existsByUsername(username)
}
