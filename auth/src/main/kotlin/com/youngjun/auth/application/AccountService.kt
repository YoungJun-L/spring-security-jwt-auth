package com.youngjun.auth.application

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.AccountReader
import com.youngjun.auth.domain.account.AccountWriter
import com.youngjun.auth.domain.account.NewAccount
import com.youngjun.auth.domain.token.RefreshTokenWriter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountReader: AccountReader,
    private val accountWriter: AccountWriter,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenWriter: RefreshTokenWriter,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): Account = accountReader.read(username)

    fun register(newAccount: NewAccount): Account = accountWriter.write(newAccount.encodedWith(passwordEncoder))

    fun logout(account: Account): Account {
        account.logout()
        accountWriter.write(account)
        refreshTokenWriter.expire(account)
        return account
    }

    fun login(account: Account): Account {
        account.enable()
        accountWriter.write(account)
        return account
    }
}
