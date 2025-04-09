package com.youngjun.admin.domain.administrator

import com.youngjun.admin.domain.user.EmailAddress
import org.springframework.data.jpa.repository.JpaRepository

interface AdministratorRepository : JpaRepository<Administrator, Long> {
    fun existsByEmailAddress(emailAddress: EmailAddress): Boolean

    fun findByEmailAddress(emailAddress: EmailAddress): Administrator?
}
