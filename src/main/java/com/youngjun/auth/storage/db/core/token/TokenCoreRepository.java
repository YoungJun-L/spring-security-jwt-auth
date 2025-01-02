package com.youngjun.auth.storage.db.core.token;

import com.youngjun.auth.core.domain.token.RefreshToken;
import com.youngjun.auth.core.domain.token.Token;
import com.youngjun.auth.core.domain.token.TokenPair;
import com.youngjun.auth.core.domain.token.TokenRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TokenCoreRepository implements TokenRepository {

    private final TokenJpaRepository tokenJpaRepository;

    public TokenCoreRepository(TokenJpaRepository tokenJpaRepository) {
        this.tokenJpaRepository = tokenJpaRepository;
    }

    @Override
    public void delete(Long authId) {
        tokenJpaRepository.deleteByAuthId(authId);
    }

    @Override
    public Token write(TokenPair tokenPair) {
        TokenEntity tokenEntity = new TokenEntity(tokenPair.getAuthId(), tokenPair.getRefreshToken());
        tokenJpaRepository.save(tokenEntity);
        return tokenEntity.toToken();
    }

    @Override
    public List<Token> read(RefreshToken refreshToken) {
        return tokenJpaRepository.findByRefreshToken(refreshToken.value()).stream().map(TokenEntity::toToken).toList();
    }

}
