package com.youngjun.auth.core.api.support.error

class AuthException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)
