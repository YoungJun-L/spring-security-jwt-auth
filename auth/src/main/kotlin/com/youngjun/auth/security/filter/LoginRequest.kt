package com.youngjun.auth.security.filter

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.RawPassword

data class LoginRequest(
    val email: String,
    val password: String,
) {
    fun toEmailAddress(): EmailAddress = EmailAddress(email)

    fun toRawPassword(): RawPassword = RawPassword(password)
}
