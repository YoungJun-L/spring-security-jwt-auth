package com.youngjun.auth.application

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.AccountWriter
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.domain.token.TokenPair
import com.youngjun.auth.domain.token.TokenPairGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PasswordService(
    private val accountWriter: AccountWriter,
    private val passwordEncoder: PasswordEncoder,
    private val tokenPairGenerator: TokenPairGenerator,
) {
    fun changePassword(
        account: Account,
        rawPassword: RawPassword,
        now: LocalDateTime = LocalDateTime.now(),
    ): TokenPair =
        account
            .apply { changePassword(rawPassword, passwordEncoder) }
            .let { accountWriter.write(it) }
            .let { tokenPairGenerator.generate(it.id, now) }
}
