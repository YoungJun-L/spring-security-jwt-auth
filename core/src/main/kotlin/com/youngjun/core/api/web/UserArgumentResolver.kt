package com.youngjun.core.api.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.youngjun.core.domain.User
import com.youngjun.core.support.error.CoreException
import com.youngjun.core.support.error.ErrorType.FORBIDDEN_ERROR
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver(
    private val objectMapper: ObjectMapper,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean = parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): User {
        try {
            val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            return objectMapper.readValue(request!!.cookies.first { it.name == USER_COOKIE_NAME }.value)
        } catch (ex: Exception) {
            throw CoreException(FORBIDDEN_ERROR)
        }
    }

    companion object {
        private const val USER_COOKIE_NAME = "user"
    }
}
