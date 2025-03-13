package com.youngjun.admin.web.controller.v1

import com.youngjun.admin.application.AdministratorService
import com.youngjun.admin.domain.administrator.EmailAddress
import com.youngjun.admin.domain.administrator.NewAdministrator
import com.youngjun.admin.domain.administrator.RawPassword
import com.youngjun.admin.web.controller.v1.request.SignUpRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class AdministratorController(
    private val administratorService: AdministratorService,
) {
    @GetMapping("/login")
    fun login(): String = "/login"

    @GetMapping("/signup")
    fun signUp(
        @ModelAttribute signUpRequest: SignUpRequest,
    ): String = "/signup"

    @PostMapping("/signup")
    fun processSignUp(
        @ModelAttribute signUpRequest: SignUpRequest,
        redirectAttributes: RedirectAttributes,
    ): String {
        administratorService.signUp(
            NewAdministrator(signUpRequest.name, EmailAddress(signUpRequest.email), RawPassword(signUpRequest.password)),
        )
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.")
        return "redirect:/login"
    }
}
