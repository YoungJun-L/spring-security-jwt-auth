package com.youngjun.auth.core.api.controller.v1;

import com.youngjun.auth.core.api.controller.v1.request.RegisterAuthRequestDto;
import com.youngjun.auth.core.api.controller.v1.response.RegisterAuthResponseDto;
import com.youngjun.auth.core.api.support.response.AuthResponse;
import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public AuthResponse<RegisterAuthResponseDto> register(@RequestBody @Valid RegisterAuthRequestDto request) {
        Auth auth = authService.register(request.toNewAuth());
        return AuthResponse.success(RegisterAuthResponseDto.from(auth));
    }

}
