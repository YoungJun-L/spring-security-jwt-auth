package com.youngjun.auth.core.api.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum AuthErrorType {

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, AuthErrorCode.E5000, "예상치 못한 오류가 발생하였습니다.", LogLevel.ERROR),
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, AuthErrorCode.E4000, "잘못된 입력입니다.", LogLevel.INFO),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, AuthErrorCode.E4010, "잘못된 접근입니다.", LogLevel.INFO),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, AuthErrorCode.E4030, "잘못된 접근입니다.", LogLevel.INFO),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, AuthErrorCode.E4040, "해당 정보를 찾을 수 없습니다.", LogLevel.INFO),

    // auth
    AUTH_BAD_CREDENTIALS_ERROR(HttpStatus.UNAUTHORIZED, AuthErrorCode.E4011, "아이디 또는 비밀번호가 틀립니다.", LogLevel.INFO),
    AUTH_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, AuthErrorCode.E4002, "이미 존재하는 아이디입니다.", LogLevel.INFO),
    AUTH_LOCKED_ERROR(HttpStatus.FORBIDDEN, AuthErrorCode.E4031, "이용이 제한된 유저입니다.", LogLevel.WARN),
    AUTH_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, AuthErrorCode.E4041, "계정이 존재하지 않습니다.", LogLevel.INFO),

    // token
    TOKEN_INVALID_ERROR(HttpStatus.UNAUTHORIZED, AuthErrorCode.E4012, "토큰이 유효하지 않습니다.", LogLevel.WARN),
    TOKEN_NOT_FOUND_ERROR(HttpStatus.UNAUTHORIZED, AuthErrorCode.E4012, "토큰이 유효하지 않습니다.", LogLevel.WARN),
    TOKEN_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, AuthErrorCode.E4013, "토큰이 만료되었습니다.", LogLevel.INFO);

    private final HttpStatus status;

    private final AuthErrorCode code;

    private final String message;

    private final LogLevel logLevel;

    AuthErrorType(HttpStatus status, AuthErrorCode code, String message, LogLevel logLevel) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public AuthErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}
