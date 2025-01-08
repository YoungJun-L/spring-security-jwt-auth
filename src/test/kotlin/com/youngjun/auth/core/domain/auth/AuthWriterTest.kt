package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.AUTH_DUPLICATE_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import com.youngjun.auth.core.storage.db.core.auth.AuthEntityBuilder
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe

@DomainTest
class AuthWriterTest(
    private val authWriter: AuthWriter,
    private val authJpaRepository: AuthJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("저장") {
                test("성공") {
                    val newAuth = NewAuthBuilder().build()

                    val actual = authWriter.write(newAuth)

                    actual.username shouldBe newAuth.username
                }

                test("동일한 이름으로 저장하면 실패한다.") {
                    val username = "username123"
                    authJpaRepository.save(AuthEntityBuilder(username = username).build())

                    val newAuth = NewAuthBuilder(username = username).build()
                    shouldThrow<AuthException> { authWriter.write(newAuth) }
                        .errorType shouldBe AUTH_DUPLICATE_ERROR
                }
            }
        },
    )
