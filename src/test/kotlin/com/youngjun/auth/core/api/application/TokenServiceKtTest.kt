package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.support.ApplicationTest
import com.youngjun.auth.core.domain.auth.AuthBuilder
import com.youngjun.auth.core.domain.token.RefreshTokenBuilder
import com.youngjun.auth.core.domain.token.TokenService
import com.youngjun.auth.core.storage.db.core.auth.AuthEntityBuilder
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository
import com.youngjun.auth.storage.db.core.token.TokenEntity
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.time.withConstantNow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Value
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneOffset
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@ApplicationTest
class TokenServiceKtTest(
    private val tokenService: TokenService,
    private val tokenJpaRepository: TokenJpaRepository,
    private val authJpaRepository: AuthJpaRepository,
    @Value("\${spring.security.jwt.secret-key}") private val secretKey: String,
    @Value("\${spring.security.jwt.exp.refresh}") private val refreshExp: Long,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("토큰 발급") {
                test("성공") {
                    val auth = AuthBuilder().build()

                    val actual = tokenService.issue(auth)

                    actual.authId shouldBe auth.id
                }

                test("access token 은 30분간 유효하다.") {
                    val now = now()
                    withConstantNow(now) {
                        val auth = AuthBuilder().build()

                        val actual = tokenService.issue(auth)

                        actual.accessTokenExpiration shouldBe now.toEpochSecond(ZoneOffset.UTC) + 30.minutes.inWholeSeconds
                    }
                }

                test("refresh token 은 30일간 유효하다.") {
                    val now = now()
                    withConstantNow(now) {
                        val auth = AuthBuilder().build()

                        val actual = tokenService.issue(auth)

                        actual.refreshTokenExpiration shouldBe now.toEpochSecond(ZoneOffset.UTC) + 30.days.inWholeSeconds
                    }
                }

                test("이전 refresh token 은 제거된다") {
                    val authId = 1L
                    val auth = AuthBuilder(id = authId).build()
                    val previousToken = buildJwt(secretKey = secretKey)
                    tokenJpaRepository.save(TokenEntity(authId, previousToken))

                    tokenService.issue(auth)

                    val actual = tokenJpaRepository.findByAuthId(authId)
                    actual shouldHaveSize 1
                    actual[0] shouldNotBe previousToken
                }
            }

            context("토큰 재발급") {
                test("성공") {
                    val authEntity = authJpaRepository.save(AuthEntityBuilder().build())
                    val refreshToken = buildJwt(secretKey = secretKey)
                    tokenJpaRepository.save(TokenEntity(authEntity.id, refreshToken))

                    val actual = tokenService.reissue(RefreshTokenBuilder(value = refreshToken).build())

                    actual.authId shouldBe authEntity.id
                }

                test("refresh token 의 만료 기간이 1주일 이상 남았으면 refresh token 은 갱신되지 않는다.") {
                }

                test("refresh token 의 만료 기간이 1주일 미만 남았으면 refresh token 도 갱신된다.") {
                }

                test("refresh token 이 갱신되면 이전 토큰은 제거된다.") {
                }
            }
        },
    )

private fun buildJwt(
    subject: String = "username123",
    issuedAt: LocalDateTime = now(),
    expiresInSeconds: Long = 30.days.inWholeSeconds,
    secretKey: String = "",
    extraClaims: Map<String, Any> = emptyMap(),
): String {
    val expiration = issuedAt.toEpochSecond(ZoneOffset.UTC) + expiresInSeconds
    return Jwts
        .builder()
        .subject(subject)
        .issuedAt(Date(issuedAt.toInstant(ZoneOffset.UTC).toEpochMilli()))
        .expiration(Date(expiration * 1_000))
        .claims(extraClaims)
        .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
        .compact()
}
