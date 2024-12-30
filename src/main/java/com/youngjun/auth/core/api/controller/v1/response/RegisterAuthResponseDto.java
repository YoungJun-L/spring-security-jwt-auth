package com.youngjun.auth.core.api.controller.v1.response;

import com.youngjun.auth.core.domain.auth.Auth;

public record RegisterAuthResponseDto(Long userId) {
	public static RegisterAuthResponseDto from(Auth auth) {
		return new RegisterAuthResponseDto(auth.id());
	}
}
