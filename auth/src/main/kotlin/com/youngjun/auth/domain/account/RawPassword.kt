package com.youngjun.auth.domain.account

data class RawPassword(
    val value: String,
) {
    init {
        require(value.length in 8..<65) { "Password validation error" }
    }
}
