package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.storage.db.core.token.TokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull

@DomainTest
class TokenWriterTest(
    private val tokenWriter: TokenWriter,
    private val tokenJpaRepository: TokenJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("토큰 교체") {
                test("성공") {
                    val newToken = NewTokenBuilder().build()
                    tokenJpaRepository.save(TokenEntityBuilder(newToken.userId, "previousToken").build())

                    val actual = tokenWriter.replace(newToken)

                    actual.refreshToken.value shouldBe newToken.refreshToken
                }

                test("이전 토큰은 제거된다.") {
                    val newToken = NewTokenBuilder().build()
                    val tokenEntity = tokenJpaRepository.save(TokenEntityBuilder(newToken.userId, "previousToken").build())

                    tokenWriter.replace(newToken)

                    tokenJpaRepository.findByIdOrNull(tokenEntity.id) shouldBe null
                }
            }

            context("토큰 값 변경") {
                test("성공") {
                    val newToken = NewTokenBuilder().build()
                    val tokenEntity = tokenJpaRepository.save(TokenEntityBuilder(newToken.userId, "previousToken").build())

                    tokenWriter.update(newToken)

                    val actual = tokenJpaRepository.findByIdOrNull(tokenEntity.id)!!
                    actual.refreshToken shouldBe newToken.refreshToken
                }
            }
        },
    )
