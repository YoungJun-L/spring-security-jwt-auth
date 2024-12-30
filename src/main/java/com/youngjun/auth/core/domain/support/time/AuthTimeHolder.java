package com.youngjun.auth.core.domain.support.time;

import org.springframework.stereotype.Component;

@Component
public class AuthTimeHolder implements TimeHolder {

	@Override
	public Long now() {
		return System.currentTimeMillis();
	}

}
