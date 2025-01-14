package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.support.ApplicationTest
import com.youngjun.auth.core.api.support.VALID_PASSWORD
import com.youngjun.auth.core.domain.user.NewUserBuilder
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.security.crypto.password.PasswordEncoder

@ApplicationTest
class UserServiceTest(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("회원 가입") {
                test("성공") {
                    val newUser = NewUserBuilder().build()

                    val actual = userService.register(newUser)

                    actual.username shouldBe newUser.username
                }

                test("비밀번호는 인코딩된다.") {
                    val newUser = NewUserBuilder(password = VALID_PASSWORD).build()

                    val actual = userService.register(newUser)

                    passwordEncoder.matches(newUser.password, actual.password) shouldBe true
                }
            }
        },
    )
