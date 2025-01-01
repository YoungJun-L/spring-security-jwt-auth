package com.youngjun.auth.core.api.support.response

import com.youngjun.auth.core.api.support.error.ErrorMessage
import com.youngjun.auth.core.api.support.error.ErrorType

data class AuthResponse<T> private constructor(
    val status: ResultType,
    val data: T? = null,
    val error: ErrorMessage? = null,
) {
    companion object {
        fun success(): AuthResponse<Any> = AuthResponse(ResultType.SUCCESS, null, null)

        fun <S> success(data: S): AuthResponse<S> = AuthResponse(ResultType.SUCCESS, data, null)

        fun error(
            error: ErrorType,
            errorData: Any? = null,
        ): AuthResponse<Any> = AuthResponse(ResultType.ERROR, null, ErrorMessage(error, errorData))
    }
}
