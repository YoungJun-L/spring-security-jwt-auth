package com.youngjun.auth.security.filter

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.RawPassword

data class LoginRequest(
    val email: EmailAddress,
    val password: RawPassword,
)
