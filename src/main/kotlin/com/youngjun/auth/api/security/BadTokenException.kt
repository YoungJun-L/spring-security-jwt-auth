package com.youngjun.auth.api.security

import org.springframework.security.core.AuthenticationException

class BadTokenException(
    msg: String?,
    override val cause: Throwable? = null,
) : AuthenticationException(msg, cause)
