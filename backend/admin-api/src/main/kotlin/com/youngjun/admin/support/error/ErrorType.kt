package com.youngjun.admin.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(
    val status: HttpStatus,
    val code: ErrorCode,
    val message: String,
    val logLevel: LogLevel,
) {
    DEFAULT(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E5000, "예상치 못한 오류가 발생하였습니다.", LogLevel.ERROR),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.E4000, "잘못된 입력입니다.", LogLevel.INFO),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E4010, "잘못된 접근입니다.", LogLevel.INFO),
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.E4030, "잘못된 접근입니다.", LogLevel.INFO),
    NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E4040, "해당 정보를 찾을 수 없습니다.", LogLevel.INFO),

    // administrator
    ADMINISTRATOR_BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, ErrorCode.E4011, "이메일 또는 비밀번호가 다릅니다.", LogLevel.INFO),
    ADMINISTRATOR_DUPLICATE(HttpStatus.BAD_REQUEST, ErrorCode.E4001, "이미 가입된 계정입니다.", LogLevel.INFO),
    ADMINISTRATOR_LOCKED(HttpStatus.FORBIDDEN, ErrorCode.E4031, "관리자 승인이 필요합니다.", LogLevel.WARN),

    // template
    TEMPLATE_VARIABLES_NOT_MATCH(HttpStatus.BAD_REQUEST, ErrorCode.E4002, "템플릿 변수가 일치하지 않습니다.", LogLevel.INFO),
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E4042, "해당 템플릿을 찾을 수 없습니다.", LogLevel.INFO),
}
