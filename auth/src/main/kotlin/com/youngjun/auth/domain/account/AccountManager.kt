package com.youngjun.auth.domain.account

import com.youngjun.auth.domain.token.RefreshTokenRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class AccountManager(
    private val accountRepository: AccountRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    @Transactional
    fun logout(account: Account): Account {
        account.logout()
        accountRepository.save(account)
        refreshTokenRepository.read(account)?.expire()
        return account
    }

    fun login(account: Account): Account {
        account.enable()
        accountRepository.save(account)
        return account
    }
}
