package com.youngjun.admin.domain.template

import com.youngjun.admin.domain.user.EmailAddress

data class TemplatedRecipient(
    val email: EmailAddress,
    val variables: Map<String, String>,
)
