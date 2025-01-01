package com.youngjun.auth.core.domain.auth;

import com.youngjun.auth.core.api.support.error.AuthException;
import com.youngjun.auth.core.api.support.error.ErrorType;
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
                .orElseThrow(() -> new UsernameNotFoundException(ErrorType.AUTH_NOT_FOUND_ERROR.getMessage()));
    }

    public Auth readEnabled(Long id) {
        Auth auth = authRepository.read(id).orElseThrow(() -> new AuthException(ErrorType.UNAUTHORIZED_ERROR, null));
        auth.verify();
        return auth;
    }

}
