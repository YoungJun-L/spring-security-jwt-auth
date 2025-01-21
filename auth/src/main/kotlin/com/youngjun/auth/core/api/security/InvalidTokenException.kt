package com.youngjun.auth.core.api.security

import org.springframework.security.core.AuthenticationException

class InvalidTokenException(
    msg: String?,
    cause: Throwable? = null,
) : AuthenticationException(msg, cause)
