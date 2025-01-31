package com.youngjun.auth.core.domain.account

import com.youngjun.auth.storage.db.core.account.AccountRepository
import com.youngjun.auth.storage.db.core.token.RefreshTokenRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class AccountManager(
    private val accountRepository: AccountRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    @Transactional
    fun logout(account: Account): Account =
        account.logout().also {
            accountRepository.update(it)
            refreshTokenRepository.expire(it)
        }

    fun login(account: Account) =
        account.enabled().also {
            accountRepository.update(it)
        }
}
