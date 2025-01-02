package com.youngjun.auth.core.domain.token;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class TokenWriter {

    private final TokenRepository tokenRepository;

    public TokenWriter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void replaceTo(TokenPair tokenPair) {
        tokenRepository.delete(tokenPair.getAuthId());
        tokenRepository.write(tokenPair);
    }

}
