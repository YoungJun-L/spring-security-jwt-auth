package com.youngjun.auth.support.error

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

    // account
    ACCOUNT_BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, ErrorCode.E4011, "이메일 또는 비밀번호가 다릅니다.", LogLevel.INFO),
    ACCOUNT_DUPLICATE(HttpStatus.BAD_REQUEST, ErrorCode.E4002, "이미 존재하는 이메일입니다.", LogLevel.INFO),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, ErrorCode.E4031, "계정이 잠겼습니다.", LogLevel.WARN),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, ErrorCode.E4032, "이용이 제한된 유저입니다.", LogLevel.WARN),
    ACCOUNT_LOGOUT(HttpStatus.FORBIDDEN, ErrorCode.E4033, "로그인이 필요합니다.", LogLevel.WARN),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E4041, "계정이 존재하지 않습니다.", LogLevel.INFO),

    // token
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, ErrorCode.E4012, "토큰이 유효하지 않습니다.", LogLevel.WARN),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, ErrorCode.E4012, "토큰이 유효하지 않습니다.", LogLevel.WARN),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCode.E4013, "토큰이 만료되었습니다.", LogLevel.INFO),

    // verification code
    VERIFICATION_CODE_MISMATCHED(HttpStatus.UNAUTHORIZED, ErrorCode.E4014, "인증 코드가 일치하지 않습니다.", LogLevel.INFO),
    VERIFICATION_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCode.E4015, "인증 코드가 만료되었습니다.", LogLevel.WARN),
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E4042, "이메일 인증이 필요합니다.", LogLevel.WARN),
    VERIFICATION_CODE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.E4291, "인증 코드 요청 가능 횟수를 초과했습니다.", LogLevel.WARN),
}
