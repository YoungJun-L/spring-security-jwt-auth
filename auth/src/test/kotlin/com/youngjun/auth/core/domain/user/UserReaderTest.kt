package com.youngjun.auth.core.domain.user

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.UNAUTHORIZED_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import com.youngjun.auth.storage.db.core.user.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.core.userdetails.UsernameNotFoundException

@DomainTest
class UserReaderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val userRepository = mockk<UserRepository>()
            val userReader = UserReader(userRepository)

            context("회원 조회") {
                test("성공") {
                    val user = UserBuilder().build()
                    every { userRepository.read(any<String>()) } returns user

                    val actual = userReader.read(user.username)

                    actual.id shouldBe user.id
                }

                test("회원이 존재하지 않는 경우 실패한다.") {
                    every { userRepository.read(any<String>()) } returns null

                    shouldThrow<UsernameNotFoundException> { userReader.read("username123") }
                }
            }

            context("이용 가능한 회원 조회") {
                test("성공") {
                    val user = UserBuilder(status = UserStatus.ENABLED).build()
                    every { userRepository.read(any<Long>()) } returns user

                    val actual = userReader.readEnabled(user.id)

                    actual.id shouldBe user.id
                }

                test("회원이 존재하지 않는 경우 실패한다.") {
                    every { userRepository.read(any<Long>()) } returns null

                    shouldThrow<AuthException> { userReader.readEnabled(1L) }.errorType shouldBe UNAUTHORIZED_ERROR
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val user = UserBuilder(status = UserStatus.DISABLED).build()
                    every { userRepository.read(any<Long>()) } returns user

                    shouldThrow<AuthException> { userReader.readEnabled(user.id) }
                }
            }
        },
    )
