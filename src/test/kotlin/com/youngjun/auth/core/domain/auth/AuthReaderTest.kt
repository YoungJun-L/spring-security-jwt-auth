package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.UNAUTHORIZED_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import com.youngjun.auth.core.storage.db.core.auth.AuthEntityBuilder
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.security.core.userdetails.UsernameNotFoundException

@DomainTest
class AuthReaderTest(
    private val authReader: AuthReader,
    private val authJpaRepository: AuthJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("회원 조회") {
                test("성공") {
                    val authEntity = authJpaRepository.save(AuthEntityBuilder().build())

                    val actual = authReader.read(authEntity.username)

                    actual.id shouldBe authEntity.id
                }

                test("회원이 존재하지 않는 경우 실패한다.") {
                    shouldThrow<UsernameNotFoundException> { authReader.read("username") }
                }
            }

            context("이용 가능한 회원 조회") {
                test("성공") {
                    val authEntity = authJpaRepository.save(AuthEntityBuilder(status = AuthStatus.ENABLED).build())

                    val actual = authReader.readEnabled(authEntity.id)

                    actual.id shouldBe authEntity.id
                }

                test("회원이 존재하지 않는 경우 실패한다.") {
                    shouldThrow<AuthException> { authReader.readEnabled(1L) }
                        .errorType shouldBe UNAUTHORIZED_ERROR
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val authEntity = authJpaRepository.save(AuthEntityBuilder(status = AuthStatus.DISABLED).build())

                    shouldThrow<AuthException> { authReader.readEnabled(authEntity.id) }
                }
            }
        },
    )
