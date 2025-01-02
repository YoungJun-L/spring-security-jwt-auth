package com.youngjun.auth.core.domain.token;

import java.util.List;

public interface TokenRepository {
    void delete(Long authId);

    Token write(TokenPair tokenPair);

    List<Token> read(RefreshToken refreshToken);
}
