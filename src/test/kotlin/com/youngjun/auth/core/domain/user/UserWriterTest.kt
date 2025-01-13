package com.youngjun.auth.core.domain.user

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.USER_DUPLICATE_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import com.youngjun.auth.storage.db.core.user.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

@DomainTest
class UserWriterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val userRepository = mockk<UserRepository>()
            val userWriter = UserWriter(userRepository)

            context("저장") {
                test("성공") {
                    val newUser = NewUserBuilder().build()
                    every { userRepository.existsByUsername(any()) } returns false
                    every { userRepository.write(any()) } returns UserBuilder(username = newUser.username).build()

                    val actual = userWriter.write(newUser)

                    actual.username shouldBe newUser.username
                }

                test("동일한 이름으로 저장하면 실패한다.") {
                    every { userRepository.existsByUsername(any()) } returns true

                    shouldThrow<AuthException> { userWriter.write(NewUserBuilder(username = "username123").build()) }
                        .errorType shouldBe USER_DUPLICATE_ERROR
                }
            }
        },
    )
