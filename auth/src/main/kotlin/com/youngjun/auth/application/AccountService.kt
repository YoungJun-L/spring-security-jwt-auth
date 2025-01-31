package com.youngjun.auth.application

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.AccountManager
import com.youngjun.auth.domain.account.AccountReader
import com.youngjun.auth.domain.account.AccountWriter
import com.youngjun.auth.domain.account.NewAccount
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountReader: AccountReader,
    private val accountWriter: AccountWriter,
    private val accountManager: AccountManager,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): Account = accountReader.read(username)

    fun register(newAccount: NewAccount): Account = accountWriter.write(newAccount.encodedWith(passwordEncoder))

    fun logout(account: Account): Account = accountManager.logout(account)

    fun login(account: Account) = accountManager.login(account)
}
