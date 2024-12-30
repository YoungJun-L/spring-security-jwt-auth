package com.youngjun.auth.core.api.support.error;

public record AuthErrorMessage(AuthErrorCode code, String message, Object data) {
	public AuthErrorMessage(AuthErrorType authErrorType) {
		this(authErrorType.getCode(), authErrorType.getMessage(), null);
	}

	public AuthErrorMessage(AuthErrorType authErrorType, Object data) {
		this(authErrorType.getCode(), authErrorType.getMessage(), data);
	}
}
