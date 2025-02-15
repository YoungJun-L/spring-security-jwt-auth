package com.youngjun.auth.domain.account

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE_ERROR
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED_ERROR
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class AccountReader(
    private val accountRepository: AccountRepository,
) {
    fun read(emailAddress: EmailAddress): Account =
        accountRepository.findBy(emailAddress) ?: throw UsernameNotFoundException("Cannot find the user")

    fun readEnabled(id: Long): Account {
        val account = accountRepository.findBy(id) ?: throw AuthException(UNAUTHORIZED_ERROR)
        account.verify()
        return account
    }

    fun validateUniqueEmailAddress(emailAddress: EmailAddress) {
        if (accountRepository.existsBy(emailAddress)) {
            throw AuthException(ACCOUNT_DUPLICATE_ERROR)
        }
    }
}
