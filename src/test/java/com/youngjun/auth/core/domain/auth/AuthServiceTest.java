package com.youngjun.auth.core.domain.auth;

import com.youngjun.auth.core.api.support.error.AuthErrorType;
import com.youngjun.auth.core.domain.support.DomainTest;
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

@DomainTest
class AuthServiceTest {

    private static final String VALID_USERNAME = "username123";

    private static final String VALID_PASSWORD = "password123!";

    private final AuthService authService;

    private final AuthRepository authRepository;

    private final AuthJpaRepository authJpaRepository;

    private final PasswordEncoder passwordEncoder;

    AuthServiceTest(AuthService authService, AuthRepository authRepository, AuthJpaRepository authJpaRepository,
                    PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.authRepository = authRepository;
        this.authJpaRepository = authJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @AfterEach
    void tearDown() {
        authJpaRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입 성공")
    @Test
    void register() {
        // given
        NewAuth newAuth = new NewAuth(VALID_USERNAME, VALID_PASSWORD, AuthStatus.ENABLED);

        // when
        authService.register(newAuth);

        // then
        Auth actual = authRepository.read(VALID_USERNAME).orElseThrow();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual.getUsername()).isEqualTo(newAuth.username());
            softly.assertThat(passwordEncoder.matches(newAuth.password(), actual.getPassword())).isTrue();
            softly.assertThat(actual.status()).isEqualTo(AuthStatus.ENABLED);
        });
    }

    @DisplayName("회원가입 시 아이디가 중복되면 실패한다.")
    @Test
    void registerWithDuplicateUsername() {
        // given
        NewAuth newAuth = new NewAuth(VALID_USERNAME, VALID_PASSWORD, AuthStatus.ENABLED);
        authRepository.write(newAuth);

        // when & then
        Assertions.assertThatThrownBy(() -> authService.register(newAuth))
                .hasFieldOrPropertyWithValue("authErrorType", AuthErrorType.AUTH_DUPLICATE_ERROR);
    }

    @DisplayName("회원가입 시 비밀번호는 인코딩된다.")
    @Test
    void registerShouldEncodePassword() {
        // given
        NewAuth newAuth = new NewAuth(VALID_USERNAME, VALID_PASSWORD, AuthStatus.ENABLED);

        // when
        authService.register(newAuth);

        // then
        Auth actual = authRepository.read(VALID_USERNAME).orElseThrow();
        Assertions.assertThat(passwordEncoder.matches(VALID_PASSWORD, actual.getPassword())).isTrue();
    }

}
