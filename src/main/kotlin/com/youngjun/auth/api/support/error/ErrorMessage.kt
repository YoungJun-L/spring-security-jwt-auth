package com.youngjun.auth.api.support.error

data class ErrorMessage private constructor(
    val code: String,
    val message: String,
    val data: Any? = null,
) {
    constructor(errorType: ErrorType, data: Any? = null) : this(errorType.code.name, errorType.message, data)
}
