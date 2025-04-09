package com.youngjun.admin.web.controller.v1

import com.youngjun.admin.domain.administrator.Administrator
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UserController {
    @GetMapping("/")
    fun home(
        model: Model,
        @AuthenticationPrincipal administrator: Administrator,
    ): String {
        model.addAttribute(administrator)
        return "manage-users"
    }
}
