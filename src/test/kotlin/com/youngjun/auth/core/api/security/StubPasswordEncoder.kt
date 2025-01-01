package com.youngjun.auth.core.api.security

import org.springframework.security.crypto.password.PasswordEncoder

class StubPasswordEncoder : PasswordEncoder {
    override fun encode(rawPassword: CharSequence?): String = "$rawPassword"

    override fun matches(
        rawPassword: CharSequence?,
        encodedPassword: String?,
    ): Boolean = true
}
