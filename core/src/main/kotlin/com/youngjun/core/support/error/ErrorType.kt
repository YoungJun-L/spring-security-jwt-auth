package com.youngjun.core.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(
    val status: HttpStatus,
    val code: ErrorCode,
    val message: String,
    val logLevel: LogLevel,
) {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E5000, "예상치 못한 오류가 발생하였습니다.", LogLevel.ERROR),
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E4000, "잘못된 입력입니다.", LogLevel.INFO),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, ErrorCode.E4010, "잘못된 접근입니다.", LogLevel.INFO),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, ErrorCode.E4030, "잘못된 접근입니다.", LogLevel.INFO),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, ErrorCode.E4040, "해당 정보를 찾을 수 없습니다.", LogLevel.INFO),

    SAMPLE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, ErrorCode.E4041, "해당 정보를 찾을 수 없습니다.", LogLevel.INFO),
}
