package com.youngjun.auth.security.support.error

import com.youngjun.auth.support.error.ErrorType
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED_ERROR
import org.springframework.security.core.AuthenticationException

class TypedAuthenticationException(
    val errorType: ErrorType = UNAUTHORIZED_ERROR,
    cause: Throwable? = null,
) : AuthenticationException(errorType.message, cause)
