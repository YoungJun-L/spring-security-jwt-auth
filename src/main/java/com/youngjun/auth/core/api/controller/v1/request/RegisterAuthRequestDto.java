package com.youngjun.auth.core.api.controller.v1.request;

import com.youngjun.auth.core.domain.auth.AuthStatus;
import com.youngjun.auth.core.domain.auth.NewAuth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterAuthRequestDto(
		@NotBlank @Pattern(regexp = "^(?!.*\\s)(?=.*[a-zA-Z])(?=.*\\d).{8,49}$",
				message = "Username validation error") String username,

		@NotBlank @Pattern(regexp = "^(?!.*\\s)(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^*+=-]).{10,49}$",
				message = "Password validation error") String password) {
	public NewAuth toNewAuth() {
		return new NewAuth(username, password, AuthStatus.ENABLED);
	}
}
