package com.youngjun.auth.domain.account

import org.springframework.stereotype.Component

@Component
class AccountWriter(
    private val accountRepository: AccountRepository,
) {
    fun write(account: Account): Account = accountRepository.save(account)
}
