package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

class BearerTokenResolver {
    fun resolve(request: HttpServletRequest): String {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return ""
        if (!authorization.startsWith(AUTHENTICATION_SCHEME_BEARER)) {
            return ""
        }
        val result =
            authorizationRegex.matchEntire(authorization)
                ?: throw TypedAuthenticationException(TOKEN_INVALID_ERROR)
        return result.groups[GROUP_NAME]!!.value
    }

    companion object {
        private const val AUTHENTICATION_SCHEME_BEARER = "Bearer"
        private const val GROUP_NAME = "value"

        private val authorizationRegex =
            Regex("^$AUTHENTICATION_SCHEME_BEARER (?<$GROUP_NAME>[\\w-]+\\.[\\w-]+\\.[\\w-]+)$")
    }
}
