package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.UNAUTHORIZED_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import com.youngjun.auth.storage.db.core.auth.AuthRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.core.userdetails.UsernameNotFoundException

@DomainTest
class AuthReaderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val authRepository = mockk<AuthRepository>()
            val authReader = AuthReader(authRepository)

            context("회원 조회") {
                test("성공") {
                    val auth = AuthBuilder().build()
                    every { authRepository.read(auth.username) } returns auth

                    val actual = authReader.read(auth.username)

                    actual.id shouldBe auth.id
                }

                test("회원이 존재하지 않는 경우 실패한다.") {
                    val username = "username123"
                    every { authRepository.read(username) } returns null

                    shouldThrow<UsernameNotFoundException> { authReader.read(username) }
                }
            }

            context("이용 가능한 회원 조회") {
                test("성공") {
                    val auth = AuthBuilder(status = AuthStatus.ENABLED).build()
                    every { authRepository.read(auth.id) } returns auth

                    val actual = authReader.readEnabled(auth.id)

                    actual.id shouldBe auth.id
                }

                test("회원이 존재하지 않는 경우 실패한다.") {
                    val id = 1L
                    every { authRepository.read(id) } returns null

                    shouldThrow<AuthException> { authReader.readEnabled(id) }
                        .errorType shouldBe UNAUTHORIZED_ERROR
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val auth = AuthBuilder(status = AuthStatus.DISABLED).build()
                    every { authRepository.read(auth.id) } returns auth

                    shouldThrow<AuthException> { authReader.readEnabled(auth.id) }
                }
            }
        },
    )
