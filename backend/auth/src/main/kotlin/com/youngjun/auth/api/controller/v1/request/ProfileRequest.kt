package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.Profile

data class ProfileRequest(
    val name: String,
    val nickname: String,
    val phoneNumber: String,
    val country: String,
) {
    fun toProfile(): Profile =
        Profile(
            name = name,
            nickname = nickname,
            phoneNumber = phoneNumber,
            country = country,
        )
}
