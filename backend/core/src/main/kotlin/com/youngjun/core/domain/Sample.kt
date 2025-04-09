package com.youngjun.core.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "sample")
@Entity
class Sample(
    userId: Long,
    data: String,
) : BaseEntity() {
    @Column
    var userId = userId
        private set

    @Column
    var data = data
        private set
}
