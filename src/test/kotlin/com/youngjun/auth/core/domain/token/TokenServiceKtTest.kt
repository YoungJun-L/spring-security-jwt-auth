package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.auth.Auth
import com.youngjun.auth.core.domain.auth.AuthReader
import com.youngjun.auth.core.domain.auth.AuthStatus
import com.youngjun.auth.core.domain.support.DomainTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk

@DomainTest
class TokenServiceKtTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val tokenPairGenerator = mockk<TokenPairGenerator>()
            val tokenWriter = mockk<TokenWriter>()
            val tokenReader = mockk<TokenReader>()
            val authReader = mockk<AuthReader>()
            val tokenService = TokenService(tokenPairGenerator, tokenWriter, tokenReader, authReader)

            context("토큰 발급") {
                test("성공") {
                    val auth = Auth(1L, "username", "password", AuthStatus.ENABLED)
                    every { }

                    val actual = tokenService.issue(auth)
                }
            }

//            @DisplayName("토큰 발급 성공")
//            @Test
//            fun issue() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.ENABLED)
//                val savedAuth = authJpaRepository.save(authEntity)
//
//                // when
//                val tokenPair = tokenService.issue(savedAuth.toAuth())
//
//                // then
//                Assertions.assertDoesNotThrow(ThrowingSupplier<Jwt<Any, Any>> { jwtParser.parse(tokenPair.accessToken) })
//                Assertions.assertDoesNotThrow(ThrowingSupplier<Jwt<Any, Any>> { jwtParser.parse(tokenPair.refreshToken) })
//            }
//
//            @DisplayName("토큰 발급 시 refresh token 이 저장된다.")
//            @Test
//            fun issueShouldSaveRefreshToken() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.ENABLED)
//                val savedAuth = authJpaRepository.save(authEntity)
//
//                // when
//                val tokenPair = tokenService.issue(savedAuth.toAuth())
//
//                // then
//                val tokenEntities = tokenJpaRepository.findByAuthId(savedAuth.id)
//                org.assertj.core.api.Assertions
//                    .assertThat(tokenEntities)
//                    .hasSize(1)
//                org.assertj.core.api.Assertions
//                    .assertThat(tokenEntities[0].refreshToken)
//                    .isEqualTo(tokenPair.refreshToken)
//            }
//
//            @DisplayName("토큰 발급 시 access token 은 30분간 유효하다.")
//            @Test
//            fun issueAccessTokenValidFor30Minutes() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.ENABLED)
//                val savedAuth = authJpaRepository.save(authEntity)
//
//                // when
//                val tokenPair = tokenService.issue(savedAuth.toAuth())
//
//                // then
//                val actual =
//                    jwtParser
//                        .parseSignedClaims(tokenPair.accessToken)
//                        .payload.expiration.time
//                val expected = timeHolder.now() + Duration.ofMinutes(30L).toMillis()
//                org.assertj.core.api.Assertions
//                    .assertThat(actual)
//                    .isCloseTo(expected, Offset.offset(1000L))
//            }
//
//            @DisplayName("토큰 발급 시 refresh token 은 30일간 유효하다.")
//            @Test
//            fun issueRefreshTokenValidFor30Days() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.ENABLED)
//                val savedAuth = authJpaRepository.save(authEntity)
//
//                // when
//                val tokenPair = tokenService.issue(savedAuth.toAuth())
//
//                // then
//                val actual =
//                    jwtParser
//                        .parseSignedClaims(tokenPair.refreshToken)
//                        .payload.expiration.time
//                val expected = timeHolder.now() + Duration.ofDays(30L).toMillis()
//                org.assertj.core.api.Assertions
//                    .assertThat(actual)
//                    .isCloseTo(expected, Offset.offset(1000L))
//            }
//
//            @DisplayName("토큰 발급 시 이전 토큰은 제거된다.")
//            @Test
//            fun issueShouldRemoveOldToken() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.ENABLED)
//                val savedAuth = authJpaRepository.save(authEntity)
//
//                val tokenEntity = TokenEntity(savedAuth.id, "oldRefreshToken")
//                val savedTokenEntity = tokenJpaRepository.save(tokenEntity)
//
//                // when
//                tokenService.issue(savedAuth.toAuth())
//
//                // then
//                val tokenEntities = tokenJpaRepository.findByAuthId(savedAuth.id)
//                org.assertj.core.api.Assertions
//                    .assertThat(tokenEntities)
//                    .hasSize(1)
//                org.assertj.core.api.Assertions
//                    .assertThat(tokenEntities[0].id)
//                    .isNotEqualTo(savedTokenEntity.id)
//            }
//
//            @DisplayName("토큰 재발급 성공")
//            @Test
//            fun reissue() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.ENABLED)
//                val savedAuth = authJpaRepository.save(authEntity)
//
//                val refreshToken = buildToken(savedAuth.id, Duration.ofDays(30L).toMillis())
//                val tokenEntity = TokenEntity(savedAuth.id, refreshToken)
//                tokenJpaRepository.save(tokenEntity)
//
//                // when
//                val tokenPair = tokenService.reissue(refreshToken)
//
//                // then
//                Assertions.assertDoesNotThrow(ThrowingSupplier<Jwt<Any, Any>> { jwtParser.parse(tokenPair.accessToken) })
//                Assertions.assertDoesNotThrow(ThrowingSupplier<Jwt<Any, Any>> { jwtParser.parse(tokenPair.refreshToken) })
//            }
//
//            private fun buildToken(
//                id: Long,
//                expiration: Long,
//            ): String =
//                Jwts
//                    .builder()
//                    .claims(emptyMap<String, Any>())
//                    .subject(id.toString())
//                    .issuedAt(Date(timeHolder.now()))
//                    .expiration(Date(timeHolder.now() + expiration))
//                    .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
//                    .compact()
//
//            @DisplayName("토큰 재발급 시 refresh token 이 만료되면 실패한다.")
//            @Test
//            fun reissueWithExpiredRefreshToken() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.ENABLED)
//                val savedAuth = authJpaRepository.save(authEntity)
//                val token = buildToken(savedAuth.id, 0L)
//
//                // when & then
//                org.assertj.core.api.Assertions
//                    .assertThatThrownBy { tokenService.reissue(token) }
//                    .hasFieldOrPropertyWithValue("authErrorType", AuthErrorType.TOKEN_EXPIRED_ERROR)
//            }
//
//            @DisplayName("토큰 재발급 시 가입하지 않은 회원이면 실패한다.")
//            @Test
//            fun reissueWithNotRegisteredUser() {
//                // given
//                val refreshToken = buildToken(-1L, Duration.ofDays(30L).toMillis())
//
//                // when & then
//                org.assertj.core.api.Assertions
//                    .assertThatThrownBy { tokenService.reissue(refreshToken) }
//                    .hasFieldOrPropertyWithValue("authErrorType", AuthErrorType.TOKEN_NOT_FOUND_ERROR)
//            }
//
//            @DisplayName("토큰 재발급 시 서비스 가입이 제한된 회원이면 실패한다.")
//            @Test
//            fun reissueWithLockedUser() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.LOCKED)
//                val savedAuth = authJpaRepository.save(authEntity)
//
//                val refreshToken = buildToken(savedAuth.id, Duration.ofDays(30L).toMillis())
//                val tokenEntity = TokenEntity(savedAuth.id, refreshToken)
//                tokenJpaRepository.save(tokenEntity)
//
//                // when & then
//                org.assertj.core.api.Assertions
//                    .assertThatThrownBy { tokenService.reissue(refreshToken) }
//                    .hasFieldOrPropertyWithValue("authErrorType", AuthErrorType.AUTH_LOCKED_ERROR)
//            }
//
//            @DisplayName("토큰 재발급 시 이전 토큰은 제거된다.")
//            @Test
//            fun reissueShouldRemoveOldToken() {
//                // given
//                val authEntity = AuthEntity("username", "password", AuthStatus.ENABLED)
//                val savedAuth = authJpaRepository.save(authEntity)
//
//                val refreshToken = buildToken(savedAuth.id, Duration.ofDays(30L).toMillis())
//                val tokenEntity = TokenEntity(savedAuth.id, refreshToken)
//                val savedTokenEntity = tokenJpaRepository.save(tokenEntity)
//
//                // when
//                tokenService.reissue(refreshToken)
//
//                // then
//                val tokenEntities = tokenJpaRepository.findByAuthId(savedAuth.id)
//                org.assertj.core.api.Assertions
//                    .assertThat(tokenEntities)
//                    .hasSize(1)
//                org.assertj.core.api.Assertions
//                    .assertThat(tokenEntities[0].id)
//                    .isNotEqualTo(savedTokenEntity.id)
//            }
        },
    )
