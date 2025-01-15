package com.youngjun.core.api.support.response

import com.youngjun.auth.core.api.support.response.ResultType
import com.youngjun.core.api.support.error.ErrorMessage
import com.youngjun.core.api.support.error.ErrorType

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
