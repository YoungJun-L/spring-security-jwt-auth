package com.youngjun.auth.application

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.AccountReader
import com.youngjun.auth.domain.account.AccountWriter
import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.Password
import com.youngjun.auth.domain.account.Profile
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.domain.token.RefreshTokenStore
import com.youngjun.auth.domain.verificationCode.RawVerificationCode
import com.youngjun.auth.domain.verificationCode.VerificationCodeReader
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AccountService(
    private val accountReader: AccountReader,
    private val accountWriter: AccountWriter,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenStore: RefreshTokenStore,
    private val verificationCodeReader: VerificationCodeReader,
) : UserDetailsService {
    override fun loadUserByUsername(emailAddress: String): Account =
        runCatching { accountReader.read(EmailAddress(emailAddress)) }
            .getOrElse { throw UsernameNotFoundException("Cannot find the user. $emailAddress") }

    fun register(
        emailAddress: EmailAddress,
        rawPassword: RawPassword,
        profile: Profile,
        rawVerificationCode: RawVerificationCode,
        now: LocalDateTime = LocalDateTime.now(),
    ): Account {
        accountReader.checkNotDuplicate(emailAddress)
        verificationCodeReader.readLatest(emailAddress).verifyWith(rawVerificationCode, now)
        return accountWriter.write(
            Account(
                emailAddress = emailAddress,
                password = Password.encodedWith(rawPassword, passwordEncoder),
                profile = profile,
            ),
        )
    }

    fun logout(account: Account): Account =
        account
            .apply { logout() }
            .let { accountWriter.write(it) }
            .also { refreshTokenStore.expireIfExists(it) }

    fun login(account: Account): Account = account.apply { enable() }.let { accountWriter.write(it) }
}
