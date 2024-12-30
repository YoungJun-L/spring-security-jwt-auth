package com.youngjun.auth.storage.db.core.token;

import com.youngjun.auth.core.domain.token.Token;
import com.youngjun.auth.core.domain.token.TokenPair;
import com.youngjun.auth.core.domain.token.TokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TokenCoreRepository implements TokenRepository {

    private final TokenJpaRepository tokenJpaRepository;

    public TokenCoreRepository(TokenJpaRepository tokenJpaRepository) {
        this.tokenJpaRepository = tokenJpaRepository;
    }

    @Transactional
    public Token write(TokenPair tokenPair) {
        Long authId = tokenPair.authId();
        if (tokenJpaRepository.existsByAuthId(authId)) {
            tokenJpaRepository.deleteByAuthId(authId);
        }
        TokenEntity tokenEntity = new TokenEntity(authId, tokenPair.refreshToken());
        tokenJpaRepository.save(tokenEntity);
        return tokenEntity.toToken();
    }

    @Override
    public List<Token> read(String refreshToken) {
        return tokenJpaRepository.findByRefreshToken(refreshToken).stream().map(TokenEntity::toToken).toList();
    }

}
