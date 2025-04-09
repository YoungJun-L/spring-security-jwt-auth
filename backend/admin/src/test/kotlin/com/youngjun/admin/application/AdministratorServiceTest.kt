package com.youngjun.admin.application

import com.youngjun.admin.domain.AdministratorBuilder
import com.youngjun.admin.domain.EMAIL_ADDRESS
import com.youngjun.admin.domain.NAME
import com.youngjun.admin.domain.RAW_PASSWORD
import com.youngjun.admin.domain.administrator.AdministratorRepository
import com.youngjun.admin.domain.administrator.AdministratorStatus
import com.youngjun.admin.domain.administrator.NewAdministrator
import com.youngjun.admin.support.ApplicationContextTest
import com.youngjun.admin.support.error.AdminException
import com.youngjun.admin.support.error.ErrorType.ADMINISTRATOR_DUPLICATE
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

@ApplicationContextTest
class AdministratorServiceTest(
    private val administratorService: AdministratorService,
    private val administratorRepository: AdministratorRepository,
    private val passwordEncoder: PasswordEncoder,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("관리자 조회") {
                test("성공") {
                    val administrator = administratorRepository.save(AdministratorBuilder().build().apply { approve() })

                    val actual = administratorService.loadUserByUsername(administrator.username)

                    actual.emailAddress shouldBe administrator.emailAddress
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<UsernameNotFoundException> { administratorService.loadUserByUsername(EMAIL_ADDRESS.value) }
                }
            }

            context("관리자 가입") {
                test("성공") {
                    val actual = administratorService.signUp(NewAdministrator(NAME, EMAIL_ADDRESS, RAW_PASSWORD))

                    actual.emailAddress shouldBe EMAIL_ADDRESS
                }

                test("비밀번호는 인코딩된다.") {
                    val actual = administratorService.signUp(NewAdministrator(NAME, EMAIL_ADDRESS, RAW_PASSWORD))

                    passwordEncoder.matches(RAW_PASSWORD.value, actual.password) shouldBe true
                }

                test("중복된 이메일 주소가 존재하면 실패한다.") {
                    administratorRepository.save(AdministratorBuilder(emailAddress = EMAIL_ADDRESS).build())

                    shouldThrow<AdminException> {
                        administratorService.signUp(NewAdministrator(NAME, EMAIL_ADDRESS, RAW_PASSWORD))
                    }.errorType shouldBe ADMINISTRATOR_DUPLICATE
                }

                test("미승인 상태로 가입된다.") {
                    val actual = administratorService.signUp(NewAdministrator(NAME, EMAIL_ADDRESS, RAW_PASSWORD))

                    actual.status shouldBe AdministratorStatus.PENDING
                }
            }
        },
    )
