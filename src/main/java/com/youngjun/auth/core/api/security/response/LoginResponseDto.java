package com.youngjun.auth.core.api.security.response;

import com.youngjun.auth.core.domain.token.TokenPair;

public record LoginResponseDto(Long userId, TokenResponseDto tokens) {
	public static LoginResponseDto from(TokenPair tokenPair) {
		return new LoginResponseDto(tokenPair.authId(), TokenResponseDto.from(tokenPair));
	}
}
