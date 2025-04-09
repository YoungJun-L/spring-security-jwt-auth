package com.youngjun.admin.domain.administrator

import com.youngjun.admin.domain.user.EmailAddress
import org.springframework.security.crypto.password.PasswordEncoder

data class NewAdministrator(
    val name: String,
    val emailAddress: EmailAddress,
    val rawPassword: RawPassword,
) {
    fun encodedWith(passwordEncoder: PasswordEncoder): Administrator =
        Administrator(emailAddress, Password.encodedWith(rawPassword, passwordEncoder), name)
}
