package com.youngjun.auth.core.domain.token;

public record TokenPair(Long authId, String accessToken, Long accessTokenExpiresIn, String refreshToken,
		Long refreshTokenExpiresIn) {
}
