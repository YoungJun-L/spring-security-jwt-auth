package com.youngjun.auth.core.domain.token;

import com.youngjun.auth.api.support.error.AuthException;
import com.youngjun.auth.api.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenReader {

    private final TokenRepository tokenRepository;

    private final TokenParser tokenParser;

    public TokenReader(TokenRepository tokenRepository, TokenParser tokenParser) {
        this.tokenRepository = tokenRepository;
        this.tokenParser = tokenParser;
    }

    public Token readVerified(RefreshToken refreshToken) {
        tokenParser.verify(refreshToken);
        List<Token> tokens = tokenRepository.read(refreshToken);
        if (tokens.isEmpty()) {
            throw new AuthException(ErrorType.TOKEN_NOT_FOUND_ERROR, null);
        }
        return tokens.get(0);
    }

}
