package com.youngjun.core.support.response

import com.youngjun.core.support.error.ErrorMessage
import com.youngjun.core.support.error.ErrorType

data class ApiResponse<T> private constructor(
    val status: ResultType,
    val data: T? = null,
    val error: ErrorMessage? = null,
) {
    companion object {
        fun success(): ApiResponse<Any> = ApiResponse(ResultType.SUCCESS)

        fun <S> success(data: S): ApiResponse<S> = ApiResponse(ResultType.SUCCESS, data)

        fun error(
            error: ErrorType,
            errorData: Any? = null,
        ): ApiResponse<Any> = ApiResponse(ResultType.ERROR, null, ErrorMessage(error, errorData))
    }
}
