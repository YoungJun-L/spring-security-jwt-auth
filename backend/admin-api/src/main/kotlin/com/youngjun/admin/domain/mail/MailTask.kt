package com.youngjun.admin.domain.mail

import com.youngjun.admin.domain.support.BaseEntity
import com.youngjun.admin.domain.support.RetryInfo
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class MailTask(
    mailMessageInfo: MailMessageInfo,
    status: MailTaskStatus = MailTaskStatus.PENDING,
    retryInfo: RetryInfo = RetryInfo(),
) : BaseEntity() {
    @Embedded
    var mailMessageInfo = mailMessageInfo
        protected set

    @Enumerated(EnumType.STRING)
    var status: MailTaskStatus = status
        protected set

    @Embedded
    var retryInfo = retryInfo
        protected set

    fun markAsProcessing() {
        status = MailTaskStatus.PROCESSING
    }

    fun markAsFailed() {
        retryInfo = retryInfo.onFailure()
        status =
            if (retryInfo.isUnrecoverable()) {
                MailTaskStatus.UNRECOVERABLE
            } else {
                MailTaskStatus.FAILED
            }
    }

    fun markAsSent() {
        status = MailTaskStatus.SENT
    }
}
