package com.youngjun.auth.domain.account

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Profile(
    @Column(name = "name")
    val name: String,
    @Column(name = "nickname")
    val nickname: String,
    @Column(name = "phone_number")
    val phoneNumber: String,
    @Column(name = "country")
    val country: String,
)
