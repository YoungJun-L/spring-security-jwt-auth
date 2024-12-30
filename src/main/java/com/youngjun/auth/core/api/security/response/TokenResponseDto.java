package com.youngjun.auth.core.api.security.response;

import com.youngjun.auth.core.domain.token.TokenPair;

public record TokenResponseDto(String accessToken, Long accessTokenExpiresIn, String refreshToken,
		Long refreshTokenExpiresIn) {
	public static TokenResponseDto from(TokenPair tokenPair) {
		return new TokenResponseDto(tokenPair.accessToken(), tokenPair.accessTokenExpiresIn(), tokenPair.refreshToken(),
				tokenPair.refreshTokenExpiresIn());
	}
}
