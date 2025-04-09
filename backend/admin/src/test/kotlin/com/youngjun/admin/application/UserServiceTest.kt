package com.youngjun.admin.application

import com.youngjun.admin.domain.user.UserRepository
import com.youngjun.admin.support.ApplicationContextTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension

@ApplicationContextTest
class UserServiceTest(
    private val userService: UserService,
    private val userRepository: UserRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

//            context("유저 목록 조회") {
//                test("성공") {
//                    val userA = UserBuilder().build()
//                    val userB = UserBuilder().build()
//                    userRepository.saveAll(listOf(userA, userB))
//
//                    val actual = userService.findAllPagedByCreatedAtDesc()
//
//                    actual.size shouldBe 2
//                    actual[0].id shouldBe userA.id
//                }
//            }
        },
    )
