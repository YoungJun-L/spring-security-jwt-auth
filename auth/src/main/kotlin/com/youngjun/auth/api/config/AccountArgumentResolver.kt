package com.youngjun.auth.api.config

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.DEFAULT
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

object AccountArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean = parameter.parameterType == Account::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Account =
        try {
            (SecurityContextHolder.getContext().authentication.principal as Account)
                .also { SecurityContextHolder.clearContext() }
        } catch (ex: Exception) {
            throw AuthException(DEFAULT)
        }
}
