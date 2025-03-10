package com.youngjun.admin.domain.administrator

@JvmInline
value class RawPassword(
    val value: String,
) {
    init {
        require(value.length in 8..<65) { "Password validation error" }
    }
}
