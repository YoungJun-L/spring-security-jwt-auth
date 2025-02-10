package com.youngjun.core.api.config

import com.youngjun.core.domain.User
import com.youngjun.core.support.error.CoreException
import com.youngjun.core.support.error.ErrorType.DEFAULT_ERROR
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean = parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): User {
        try {
            val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            return User(
                request!!
                    .cookies
                    .first { it.name == "USER_ID" }
                    .value
                    .toLong(),
            )
        } catch (ex: Exception) {
            throw CoreException(DEFAULT_ERROR)
        }
    }
}
