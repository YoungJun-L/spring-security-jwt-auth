package com.youngjun.auth.core.domain.token;

import org.springframework.stereotype.Component;

@Component
public class TokenWriter {

    private final TokenRepository tokenRepository;

    public TokenWriter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token write(TokenPair tokenPair) {
        return tokenRepository.write(tokenPair);
    }

}
