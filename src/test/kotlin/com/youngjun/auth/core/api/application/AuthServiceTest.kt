package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.support.ApplicationTest
import com.youngjun.auth.core.api.support.VALID_PASSWORD
import com.youngjun.auth.core.domain.auth.NewAuthBuilder
import com.youngjun.auth.core.storage.db.core.auth.AuthEntityBuilder
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.security.crypto.password.PasswordEncoder

@ApplicationTest
class AuthServiceTest(
    private val authService: AuthService,
    private val authJpaRepository: AuthJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("회원 조회") {
                test("성공") {
                    val authEntity = authJpaRepository.save(AuthEntityBuilder().build())

                    val actual = authService.loadUserByUsername(authEntity.username)

                    actual.id shouldBe authEntity.id
                }
            }

            context("회원 가입") {
                test("성공") {
                    val newAuth = NewAuthBuilder().build()

                    val actual = authService.register(newAuth)

                    actual.username shouldBe newAuth.username
                }

                test("비밀번호는 인코딩된다.") {
                    val newAuth = NewAuthBuilder(password = VALID_PASSWORD).build()

                    val actual = authService.register(newAuth)

                    passwordEncoder.matches(newAuth.password, actual.password) shouldBe true
                }
            }
        },
    )
