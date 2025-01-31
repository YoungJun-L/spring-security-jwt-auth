package com.youngjun.auth.domain.account

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED_ERROR
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
