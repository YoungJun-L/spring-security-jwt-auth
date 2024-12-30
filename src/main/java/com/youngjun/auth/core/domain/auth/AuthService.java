package com.youngjun.auth.core.domain.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    private final AuthReader authReader;

    private final AuthWriter authWriter;

    public AuthService(AuthReader authReader, AuthWriter authWriter) {
        this.authReader = authReader;
        this.authWriter = authWriter;
    }

    @Override
    public Auth loadUserByUsername(String username) {
        return authReader.read(username);
    }

    public Auth register(NewAuth newAuth) {
        return authWriter.write(newAuth);
    }

}
