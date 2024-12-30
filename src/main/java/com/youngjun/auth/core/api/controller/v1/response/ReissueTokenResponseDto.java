package com.youngjun.auth.core.api.controller.v1.response;

import com.youngjun.auth.core.domain.token.TokenPair;

public record ReissueTokenResponseDto(String accessToken, Long accessTokenExpiresIn, String refreshToken,
		Long refreshTokenExpiresIn) {
	public static ReissueTokenResponseDto from(TokenPair tokenPair) {
		return new ReissueTokenResponseDto(tokenPair.accessToken(), tokenPair.accessTokenExpiresIn(),
				tokenPair.refreshToken(), tokenPair.refreshTokenExpiresIn());
	}
}
