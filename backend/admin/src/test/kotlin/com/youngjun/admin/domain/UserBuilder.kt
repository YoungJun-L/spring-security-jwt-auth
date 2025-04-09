package com.youngjun.admin.domain

import com.youngjun.admin.domain.user.EmailAddress
import com.youngjun.admin.domain.user.User

data class UserBuilder(
    val emailAddress: EmailAddress = EMAIL_ADDRESS,
    val name: String = NAME,
) {
    fun build(): User = User(emailAddress = emailAddress)
}
