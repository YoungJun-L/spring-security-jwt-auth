package com.youngjun.admin.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<User>
}
