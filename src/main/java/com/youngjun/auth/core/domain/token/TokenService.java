package com.youngjun.auth.core.domain.token;

import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.auth.AuthReader;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final TokenPairGenerator tokenPairGenerator;

    private final TokenWriter tokenWriter;

    private final TokenReader tokenReader;

    private final AuthReader authReader;

    public TokenService(TokenPairGenerator tokenPairGenerator, TokenWriter tokenWriter, TokenReader tokenReader, AuthReader authReader) {
        this.tokenPairGenerator = tokenPairGenerator;
        this.tokenWriter = tokenWriter;
        this.tokenReader = tokenReader;
        this.authReader = authReader;
    }

    public TokenPair issue(Auth auth) {
        TokenPair tokenPair = tokenPairGenerator.issue(auth);
        tokenWriter.replaceTo(tokenPair);
        return tokenPair;
    }

    public TokenPair reissue(RefreshToken refreshToken) {
        Token token = tokenReader.readVerified(refreshToken);
        Auth auth = authReader.readEnabled(token.getAuthId());
        TokenPair tokenPair = tokenPairGenerator.issue(auth);
        tokenWriter.replaceTo(tokenPair);
        return tokenPair;
    }

}
