package com.youngjun.auth.domain.account

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE_ERROR
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
