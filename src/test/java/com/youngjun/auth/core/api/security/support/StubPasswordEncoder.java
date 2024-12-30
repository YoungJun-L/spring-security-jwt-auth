package com.youngjun.auth.core.api.security.support;

import org.springframework.security.crypto.password.PasswordEncoder;

public class StubPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return String.valueOf(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return rawPassword.equals(encodedPassword);
	}

}
