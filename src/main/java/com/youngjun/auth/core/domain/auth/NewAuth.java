package com.youngjun.auth.core.domain.auth;

public record NewAuth(String username, String password, AuthStatus status) {
	public NewAuth encoded(String encodedPassword) {
		return new NewAuth(username, encodedPassword, status);
	}
}
