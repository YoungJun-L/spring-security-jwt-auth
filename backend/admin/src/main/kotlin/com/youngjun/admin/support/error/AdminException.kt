package com.youngjun.admin.support.error

class AdminException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)
