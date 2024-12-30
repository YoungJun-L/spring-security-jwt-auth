package com.youngjun.auth.core.domain.auth;

import com.youngjun.auth.core.api.support.error.AuthErrorType;
import com.youngjun.auth.core.api.support.error.AuthException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthReader {

    private final AuthRepository authRepository;

    public AuthReader(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public Auth read(String username) {
        return authRepository.read(username)
                .orElseThrow(() -> new UsernameNotFoundException(AuthErrorType.AUTH_NOT_FOUND_ERROR.getMessage()));
    }

    public Auth readEnabled(Long id) {
        Auth auth = authRepository.read(id).orElseThrow(() -> new AuthException(AuthErrorType.UNAUTHORIZED_ERROR));
        auth.verify();
        return auth;
    }

}
