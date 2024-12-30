package com.youngjun.auth.core.domain.token;

import com.youngjun.auth.core.DomainTest;
import com.youngjun.auth.core.api.support.error.AuthErrorType;
import com.youngjun.auth.core.domain.auth.AuthStatus;
import com.youngjun.auth.core.domain.support.time.TimeHolder;
import com.youngjun.auth.storage.db.core.auth.AuthEntity;
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository;
import com.youngjun.auth.storage.db.core.token.TokenEntity;
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DomainTest
class TokenServiceTest {

    private final TokenService tokenService;

    private final TokenJpaRepository tokenJpaRepository;

    private final AuthJpaRepository authJpaRepository;

    private final TimeHolder timeHolder;

    private final String secretKey;

    private final JwtParser jwtParser;

    TokenServiceTest(TokenService tokenService, TokenJpaRepository tokenJpaRepository,
                     AuthJpaRepository authJpaRepository, TimeHolder timeHolder,
                     @Value("${spring.security.jwt.secret-key}") String secretKey) {
        this.tokenService = tokenService;
        this.tokenJpaRepository = tokenJpaRepository;
        this.authJpaRepository = authJpaRepository;
        this.timeHolder = timeHolder;
        this.secretKey = secretKey;
        this.jwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build();
    }

    @AfterEach
    void tearDown() {
        tokenJpaRepository.deleteAllInBatch();
        authJpaRepository.deleteAllInBatch();
    }

    @DisplayName("토큰 발급 성공")
    @Test
    void issue() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        // when
        TokenPair tokenPair = tokenService.issue(savedAuth.toAuth());

        // then
        assertDoesNotThrow(() -> jwtParser.parse(tokenPair.accessToken()));
        assertDoesNotThrow(() -> jwtParser.parse(tokenPair.refreshToken()));
    }

    @DisplayName("토큰 발급 시 refresh token 이 저장된다.")
    @Test
    void issueShouldSaveRefreshToken() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        // when
        TokenPair tokenPair = tokenService.issue(savedAuth.toAuth());

        // then
        List<TokenEntity> tokenEntities = tokenJpaRepository.findByAuthId(savedAuth.getId());
        Assertions.assertThat(tokenEntities).hasSize(1);
        assertThat(tokenEntities.get(0).getRefreshToken()).isEqualTo(tokenPair.refreshToken());
    }

    @DisplayName("토큰 발급 시 access token 은 30분간 유효하다.")
    @Test
    void issueAccessTokenValidFor30Minutes() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        // when
        TokenPair tokenPair = tokenService.issue(savedAuth.toAuth());

        // then
        Long actual = jwtParser.parseSignedClaims(tokenPair.accessToken()).getPayload().getExpiration().getTime();
        Long expected = timeHolder.now() + Duration.ofMinutes(30L).toMillis();
        Assertions.assertThat(actual).isCloseTo(expected, Offset.offset(1_000L));
    }

    @DisplayName("토큰 발급 시 refresh token 은 30일간 유효하다.")
    @Test
    void issueRefreshTokenValidFor30Days() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        // when
        TokenPair tokenPair = tokenService.issue(savedAuth.toAuth());

        // then
        Long actual = jwtParser.parseSignedClaims(tokenPair.refreshToken()).getPayload().getExpiration().getTime();
        Long expected = timeHolder.now() + Duration.ofDays(30L).toMillis();
        Assertions.assertThat(actual).isCloseTo(expected, Offset.offset(1_000L));
    }

    @DisplayName("토큰 발급 시 이전 토큰은 제거된다.")
    @Test
    void issueShouldRemoveOldToken() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        TokenEntity tokenEntity = new TokenEntity(savedAuth.getId(), "oldRefreshToken");
        TokenEntity savedTokenEntity = tokenJpaRepository.save(tokenEntity);

        // when
        tokenService.issue(savedAuth.toAuth());

        // then
        List<TokenEntity> tokenEntities = tokenJpaRepository.findByAuthId(savedAuth.getId());
        Assertions.assertThat(tokenEntities).hasSize(1);
        assertThat(tokenEntities.get(0).getId()).isNotEqualTo(savedTokenEntity.getId());
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
        TokenPair tokenPair = tokenService.reissue(refreshToken);

        // then
        assertDoesNotThrow(() -> jwtParser.parse(tokenPair.accessToken()));
        assertDoesNotThrow(() -> jwtParser.parse(tokenPair.refreshToken()));
    }

    private String buildToken(Long id, Long expiration) {
        return Jwts.builder()
                .claims(Collections.emptyMap())
                .subject(String.valueOf(id))
                .issuedAt(new Date(timeHolder.now()))
                .expiration(new Date(timeHolder.now() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    @DisplayName("토큰 재발급 시 refresh token 이 만료되면 실패한다.")
    @Test
    void reissueWithExpiredRefreshToken() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.ENABLED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);
        String token = buildToken(savedAuth.getId(), 0L);

        // when & then
        Assertions.assertThatThrownBy(() -> tokenService.reissue(token))
                .hasFieldOrPropertyWithValue("authErrorType", AuthErrorType.TOKEN_EXPIRED_ERROR);
    }

    @DisplayName("토큰 재발급 시 가입하지 않은 회원이면 실패한다.")
    @Test
    void reissueWithNotRegisteredUser() {
        // given
        String refreshToken = buildToken(-1L, Duration.ofDays(30L).toMillis());

        // when & then
        Assertions.assertThatThrownBy(() -> tokenService.reissue(refreshToken))
                .hasFieldOrPropertyWithValue("authErrorType", AuthErrorType.TOKEN_NOT_FOUND_ERROR);
    }

    @DisplayName("토큰 재발급 시 서비스 가이용이 제한된 회원이면 실패한다.")
    @Test
    void reissueWithLockedUser() {
        // given
        AuthEntity authEntity = new AuthEntity("username", "password", AuthStatus.LOCKED);
        AuthEntity savedAuth = authJpaRepository.save(authEntity);

        String refreshToken = buildToken(savedAuth.getId(), Duration.ofDays(30L).toMillis());
        TokenEntity tokenEntity = new TokenEntity(savedAuth.getId(), refreshToken);
        tokenJpaRepository.save(tokenEntity);

        // when & then
        Assertions.assertThatThrownBy(() -> tokenService.reissue(refreshToken))
                .hasFieldOrPropertyWithValue("authErrorType", AuthErrorType.AUTH_LOCKED_ERROR);
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
        tokenService.reissue(refreshToken);

        // then
        List<TokenEntity> tokenEntities = tokenJpaRepository.findByAuthId(savedAuth.getId());
        Assertions.assertThat(tokenEntities).hasSize(1);
        assertThat(tokenEntities.get(0).getId()).isNotEqualTo(savedTokenEntity.getId());
    }

}
