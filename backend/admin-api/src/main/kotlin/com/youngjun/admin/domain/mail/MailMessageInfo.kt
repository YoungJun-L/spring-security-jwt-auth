package com.youngjun.admin.domain.mail

import com.youngjun.admin.domain.support.StringMapConverter
import com.youngjun.admin.domain.user.EmailAddress
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable

@Embeddable
data class MailMessageInfo(
    @Column
    val templateId: Long,
    @Column
    val recipient: EmailAddress,
    @Convert(converter = StringMapConverter::class)
    @Column
    val variables: Map<String, String>,
)
