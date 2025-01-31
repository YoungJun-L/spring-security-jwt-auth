package com.youngjun.auth.domain.account

import com.youngjun.auth.infra.db.AccountJpaRepository
import jakarta.transaction.Transactional
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

    @Transactional
    fun update(account: Account): Account {
        val accountEntity = accountJpaRepository.findByIdOrNull(account.id)!!
        accountEntity.update(account)
        return accountEntity.toAccount()
    }
}
