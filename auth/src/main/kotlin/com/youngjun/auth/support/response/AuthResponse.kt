package com.youngjun.auth.support.response

import com.youngjun.auth.support.error.ErrorMessage
import com.youngjun.auth.support.error.ErrorType

data class AuthResponse<T> private constructor(
    val status: ResultType,
    val data: T? = null,
    val error: ErrorMessage? = null,
) {
    companion object {
        fun success(): AuthResponse<Any> = AuthResponse(ResultType.SUCCESS)

        fun <S> success(data: S): AuthResponse<S> = AuthResponse(ResultType.SUCCESS, data)

        fun error(
            error: ErrorType,
            errorData: Any? = null,
        ): AuthResponse<Any> = AuthResponse(ResultType.ERROR, null, ErrorMessage(error, errorData))
    }
}
