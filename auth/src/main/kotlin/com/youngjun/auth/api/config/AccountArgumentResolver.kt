package com.youngjun.auth.api.config

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AccountArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean = parameter.parameterType == Account::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Account {
        try {
            val account = SecurityContextHolder.getContext().authentication.principal as Account
            SecurityContextHolder.clearContext()
            return account
        } catch (ex: Exception) {
            throw AuthException(ErrorType.DEFAULT_ERROR)
        }
    }
}
