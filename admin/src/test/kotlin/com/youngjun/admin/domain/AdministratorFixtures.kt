package com.youngjun.admin.domain

import com.youngjun.admin.domain.administrator.Administrator
import com.youngjun.admin.domain.administrator.Password
import com.youngjun.admin.domain.administrator.RawPassword
import org.springframework.security.crypto.password.PasswordEncoder

data class AdministratorBuilder(
    val username: String = USERNAME,
    val password: Password = Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder),
    val name: String = NAME,
) {
    fun build(): Administrator = Administrator(username = username, password = password, name = name)
}

object NoOperationPasswordEncoder : PasswordEncoder {
    override fun encode(rawPassword: CharSequence): String = "NoOp$rawPassword"

    override fun matches(
        rawPassword: CharSequence,
        encodedPassword: String,
    ): Boolean = "NoOp$rawPassword" == encodedPassword
}

const val USERNAME = "example123"
const val NAME = "Youngjun"

val RAW_PASSWORD: RawPassword = RawPassword("password123!")
