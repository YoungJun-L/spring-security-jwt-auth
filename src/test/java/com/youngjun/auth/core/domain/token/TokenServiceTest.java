package com.youngjun.auth.core.domain.token;

import com.youngjun.auth.core.api.support.ApplicationTest;
import com.youngjun.auth.core.api.support.error.ErrorType;
import com.youngjun.auth.core.domain.auth.AuthStatus;
import com.youngjun.auth.storage.db.core.auth.AuthEntity;
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository;
import com.youngjun.auth.storage.db.core.token.TokenEntity;
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ApplicationTest
class TokenServiceTest {

    private final TokenService tokenService;

    private final TokenJpaRepository tokenJpaRepository;

    private final AuthJpaRepository authJpaRepository;

    private final String secretKey;

    private final JwtParser jwtParser;

    TokenServiceTest(TokenService tokenService, TokenJpaRepository tokenJpaRepository,
                     AuthJpaRepository authJpaRepository,
                     @Value("${spring.security.jwt.secret-key}") String secretKey) {
        this.tokenService = tokenService;
        this.tokenJpaRepository = tokenJpaRepository;
        this.authJpaRepository = authJpaRepository;
        this.secretKey = secretKey;
        this.jwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build();
    }

    @AfterEach
    void tearDown() {
        tokenJpaRepository.deleteAllInBatch();
        authJpaRepository.deleteAllInBatch();
    }

    @DisplayName("토큰 재발급 성공")
    @Test
    void reissue() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        String refreshToken = buildToken(savedAuth.getId(), Duration.ofDays(30L).toMillis());
        TokenEntity tokenEntity = new TokenEntity(savedAuth.getId(), refreshToken);
        tokenJpaRepository.save(tokenEntity);

        // when
        TokenPair tokenPair = tokenService.reissue(new RefreshToken(refreshToken));

        // then
        assertDoesNotThrow(() -> jwtParser.parse(tokenPair.getAccessToken()));
        assertDoesNotThrow(() -> jwtParser.parse(tokenPair.getRefreshToken()));
    }

    private String buildToken(Long id, Long expiration) {
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return Jwts.builder()
                .claims(Collections.emptyMap())
                .subject(String.valueOf(id))
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    @DisplayName("토큰 재발급 시 refresh token 이 만료되면 실패한다.")
    @Test
    void reissueWithExpiredRefreshToken() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);
        String refreshToken = buildToken(savedAuth.getId(), 0L);

        // when & then
        Assertions.assertThatThrownBy(() -> tokenService.reissue(new RefreshToken(refreshToken)))
                .hasFieldOrPropertyWithValue("authErrorType", ErrorType.TOKEN_EXPIRED_ERROR);
    }

    @DisplayName("토큰 재발급 시 가입하지 않은 회원이면 실패한다.")
    @Test
    void reissueWithNotRegisteredUser() {
        // given
        String refreshToken = buildToken(-1L, Duration.ofDays(30L).toMillis());

        // when & then
        Assertions.assertThatThrownBy(() -> tokenService.reissue(new RefreshToken(refreshToken)))
                .hasFieldOrPropertyWithValue("authErrorType", ErrorType.TOKEN_NOT_FOUND_ERROR);
    }

    @DisplayName("토큰 재발급 시 서비스 가입이 제한된 회원이면 실패한다.")
    @Test
    void reissueWithLockedUser() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.LOCKED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        String refreshToken = buildToken(savedAuth.getId(), Duration.ofDays(30L).toMillis());
        TokenEntity tokenEntity = new TokenEntity(savedAuth.getId(), refreshToken);
        tokenJpaRepository.save(tokenEntity);

        // when & then
        Assertions.assertThatThrownBy(() -> tokenService.reissue(new RefreshToken(refreshToken)))
                .hasFieldOrPropertyWithValue("authErrorType", ErrorType.AUTH_LOCKED_ERROR);
    }

    @DisplayName("토큰 재발급 시 이전 토큰은 제거된다.")
    @Test
    void reissueShouldRemoveOldToken() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        String refreshToken = buildToken(savedAuth.getId(), Duration.ofDays(30L).toMillis());
        TokenEntity tokenEntity = new TokenEntity(savedAuth.getId(), refreshToken);
        TokenEntity savedTokenEntity = tokenJpaRepository.save(tokenEntity);

        // when
        tokenService.reissue(new RefreshToken(refreshToken));

        // then
        List<TokenEntity> tokenEntities = tokenJpaRepository.findByAuthId(savedAuth.getId());
        assertThat(tokenEntities).hasSize(1);
        assertThat(tokenEntities.get(0).getId()).isNotEqualTo(savedTokenEntity.getId());
    }

}
