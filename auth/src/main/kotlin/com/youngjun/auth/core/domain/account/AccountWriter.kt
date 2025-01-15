package com.youngjun.auth.core.domain.account

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.ACCOUNT_DUPLICATE_ERROR
import com.youngjun.auth.storage.db.core.account.AccountRepository
import org.springframework.stereotype.Component

@Component
class AccountWriter(
    private val accountRepository: AccountRepository,
) {
    fun write(newAccount: NewAccount): Account {
        if (accountRepository.existsByUsername(newAccount.username)) {
            throw AuthException(ACCOUNT_DUPLICATE_ERROR)
        }
        return accountRepository.write(newAccount)
    }
}
