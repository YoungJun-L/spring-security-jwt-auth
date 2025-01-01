package com.youngjun.auth.core.domain.auth;

import com.youngjun.auth.core.api.support.error.AuthException;
import com.youngjun.auth.core.api.support.error.ErrorType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthWriter {

    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthWriter(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Auth write(NewAuth newAuth) {
        if (authRepository.existsByUsername(newAuth.username())) {
            throw new AuthException(ErrorType.AUTH_DUPLICATE_ERROR, null);
        }
        String encodedPassword = passwordEncoder.encode(newAuth.password());
        return authRepository.write(newAuth.encoded(encodedPassword));
    }

}
