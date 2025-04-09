package com.youngjun.admin.domain

import com.youngjun.admin.domain.administrator.Administrator
import com.youngjun.admin.domain.administrator.Password
import com.youngjun.admin.domain.administrator.RawPassword
import com.youngjun.admin.domain.user.EmailAddress
import org.springframework.security.crypto.password.PasswordEncoder

data class AdministratorBuilder(
    val emailAddress: EmailAddress = EMAIL_ADDRESS,
    val password: Password = Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder),
    val name: String = NAME,
) {
    fun build(): Administrator = Administrator(emailAddress = emailAddress, password = password, name = name)
}

object NoOperationPasswordEncoder : PasswordEncoder {
    override fun encode(rawPassword: CharSequence): String = "NoOp$rawPassword"

    override fun matches(
        rawPassword: CharSequence,
        encodedPassword: String,
    ): Boolean = "NoOp$rawPassword" == encodedPassword
}

val EMAIL_ADDRESS: EmailAddress = EmailAddress("example@youngjun.com")
val RAW_PASSWORD: RawPassword = RawPassword("password123!")
const val NAME: String = "Youngjun"
