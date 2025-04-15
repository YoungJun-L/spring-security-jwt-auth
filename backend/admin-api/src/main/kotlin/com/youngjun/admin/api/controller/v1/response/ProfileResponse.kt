package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.user.AdminProfile

data class ProfileResponse(
    val name: String,
    val nickname: String,
    val phoneNumber: String,
    val country: String,
) {
    companion object {
        fun from(profile: AdminProfile): ProfileResponse =
            ProfileResponse(
                profile.name,
                profile.nickname,
                profile.phoneNumber,
                profile.country,
            )
    }
}
