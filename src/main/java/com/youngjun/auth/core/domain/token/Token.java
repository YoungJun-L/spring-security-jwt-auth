package com.youngjun.auth.core.domain.token;

public record Token(Long authId, String refreshToken) {
}
