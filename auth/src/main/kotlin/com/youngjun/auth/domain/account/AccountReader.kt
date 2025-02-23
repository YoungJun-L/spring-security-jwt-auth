package com.youngjun.auth.domain.account

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class AccountReader(
    private val accountRepository: AccountRepository,
) {
    fun read(emailAddress: EmailAddress): Account =
        accountRepository.findBy(emailAddress) ?: throw UsernameNotFoundException("Cannot find the user")

    fun read(id: Long): Account = accountRepository.findBy(id) ?: throw AuthException(UNAUTHORIZED)

    fun checkNotDuplicate(emailAddress: EmailAddress) {
        if (accountRepository.existsBy(emailAddress)) {
            throw AuthException(ACCOUNT_DUPLICATE)
        }
    }
}
