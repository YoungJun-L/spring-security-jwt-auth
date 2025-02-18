package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountReader
import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.verificationCode.VerificationCode
import com.youngjun.auth.domain.verificationCode.VerificationCodeReader
import com.youngjun.auth.domain.verificationCode.VerificationCodeWriter
import org.springframework.stereotype.Service

@Service
class VerificationCodeService(
    private val verificationCodeReader: VerificationCodeReader,
    private val verificationCodeWriter: VerificationCodeWriter,
    private val accountReader: AccountReader,
) {
    fun generate(emailAddress: EmailAddress): VerificationCode {
        accountReader.checkExists(emailAddress)
        verificationCodeReader.checkRecentSavesExceeded(emailAddress)
        return VerificationCode.generate(emailAddress).also { verificationCodeWriter.write(it) }
    }
}
