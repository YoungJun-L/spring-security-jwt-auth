package com.youngjun.auth.security.support.error

import com.youngjun.auth.support.error.ErrorType
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED
import org.springframework.security.core.AuthenticationException

class TypedAuthenticationException(
    val errorType: ErrorType = UNAUTHORIZED,
    cause: Throwable? = null,
) : AuthenticationException(errorType.message, cause)
