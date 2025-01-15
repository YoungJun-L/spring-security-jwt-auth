package com.youngjun.storage.db.core

import org.springframework.data.jpa.repository.JpaRepository

interface SampleJpaRepository : JpaRepository<SampleEntity, Long> {
    fun findByUserId(userId: Long): SampleEntity?
}
