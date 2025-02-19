package com.youngjun.auth.security.token

import com.youngjun.auth.security.support.error.TypedAuthenticationException
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

object BearerTokenResolver {
    private const val AUTHENTICATION_SCHEME_BEARER = "Bearer"
    private const val GROUP_NAME = "value"
    private val authorizationRegex = Regex("^$AUTHENTICATION_SCHEME_BEARER (?<$GROUP_NAME>[\\w-]+\\.[\\w-]+\\.[\\w-]+)$")

    fun resolve(request: HttpServletRequest): String {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authorization.isNullOrBlank() || !authorization.startsWith(AUTHENTICATION_SCHEME_BEARER)) {
            return ""
        }
        val result = authorizationRegex.matchEntire(authorization) ?: throw TypedAuthenticationException(TOKEN_INVALID)
        return result.groups[GROUP_NAME]!!.value
    }
}
