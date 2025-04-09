package com.youngjun.admin.application

import com.youngjun.admin.domain.user.User
import com.youngjun.admin.domain.user.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findAllPagedByCreatedAtDesc(nextId: Long?): List<User> = userRepository.findAllByOrderByCreatedAtDesc()
}
