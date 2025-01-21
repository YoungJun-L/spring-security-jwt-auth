package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.support.error.ErrorType
import com.youngjun.auth.core.support.error.ErrorType.UNAUTHORIZED_ERROR
import org.springframework.security.core.AuthenticationException

class TypedAuthenticationException(
    val errorType: ErrorType = UNAUTHORIZED_ERROR,
    cause: Throwable? = null,
) : AuthenticationException(errorType.message, cause)
