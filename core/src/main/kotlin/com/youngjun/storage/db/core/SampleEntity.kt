package com.youngjun.storage.db.core

import com.youngjun.core.domain.Sample
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "sample")
@Entity
class SampleEntity(
    val userId: Long,
    val data: String,
) : BaseEntity() {
    fun toSample(): Sample = Sample(id, userId, data)
}
