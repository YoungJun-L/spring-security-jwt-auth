package com.youngjun.admin.domain.user

import jakarta.persistence.Embeddable

@Embeddable
data class AdminProfile(
    val name: String,
    val nickname: String,
    val phoneNumber: String,
    val country: String,
)
