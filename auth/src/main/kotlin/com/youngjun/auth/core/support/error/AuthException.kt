package com.youngjun.auth.core.support.error

class AuthException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)
