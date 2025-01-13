package com.youngjun.auth.core.api.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.BadCredentialsException

class BearerTokenResolver {
    fun resolve(request: HttpServletRequest): String {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!authorization.startsWith(AUTHENTICATION_SCHEME_BEARER)) {
            return ""
        }
        val result = authorizationRegex.matchEntire(authorization) ?: throw BadCredentialsException("잘못된 접근입니다.")
        return result.groups[GROUP_NAME]!!.value
    }

    companion object {
        private const val AUTHENTICATION_SCHEME_BEARER = "Bearer"
        private const val GROUP_NAME = "value"

        private val authorizationRegex =
            Regex("^$AUTHENTICATION_SCHEME_BEARER (?<$GROUP_NAME>[\\w-]+\\.[\\w-]+\\.[\\w-]+)$")
    }
}
