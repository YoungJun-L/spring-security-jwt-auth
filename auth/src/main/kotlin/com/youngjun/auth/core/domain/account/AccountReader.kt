package com.youngjun.auth.core.domain.account

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.UNAUTHORIZED_ERROR
import com.youngjun.auth.storage.db.core.account.AccountRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class AccountReader(
    private val accountRepository: AccountRepository,
) {
    fun read(username: String): Account = accountRepository.read(username) ?: throw UsernameNotFoundException("Cannot find user")

    fun readEnabled(id: Long): Account {
        val account = accountRepository.read(id) ?: throw AuthException(UNAUTHORIZED_ERROR)
        account.verify()
        return account
    }
}
