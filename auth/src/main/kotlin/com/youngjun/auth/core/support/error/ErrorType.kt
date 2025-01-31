package com.youngjun.auth.core.support.error

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

    // account
    ACCOUNT_BAD_CREDENTIALS_ERROR(HttpStatus.UNAUTHORIZED, ErrorCode.E4011, "아이디 또는 비밀번호가 다릅니다.", LogLevel.INFO),
    ACCOUNT_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E4002, "이미 존재하는 아이디입니다.", LogLevel.INFO),
    ACCOUNT_LOCKED_ERROR(HttpStatus.FORBIDDEN, ErrorCode.E4031, "계정이 잠겼습니다.", LogLevel.WARN),
    ACCOUNT_DISABLED_ERROR(HttpStatus.FORBIDDEN, ErrorCode.E4032, "이용이 제한된 유저입니다.", LogLevel.WARN),
    ACCOUNT_LOGOUT_ERROR(HttpStatus.FORBIDDEN, ErrorCode.E4033, "로그인이 필요합니다.", LogLevel.WARN),
    ACCOUNT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, ErrorCode.E4041, "계정이 존재하지 않습니다.", LogLevel.INFO),

    // token
    TOKEN_INVALID_ERROR(HttpStatus.UNAUTHORIZED, ErrorCode.E4012, "토큰이 유효하지 않습니다.", LogLevel.WARN),
    TOKEN_NOT_FOUND_ERROR(HttpStatus.UNAUTHORIZED, ErrorCode.E4012, "토큰이 유효하지 않습니다.", LogLevel.WARN),
    TOKEN_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, ErrorCode.E4013, "토큰이 만료되었습니다.", LogLevel.INFO),
}
