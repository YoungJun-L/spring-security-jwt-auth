package com.youngjun.auth.core.api.controller.v1;

import com.youngjun.auth.core.api.controller.v1.request.ReissueTokenRequestDto;
import com.youngjun.auth.core.api.controller.v1.response.ReissueTokenResponseDto;
import com.youngjun.auth.core.api.support.response.AuthResponse;
import com.youngjun.auth.core.domain.token.TokenPair;
import com.youngjun.auth.core.domain.token.TokenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/auth/token")
    public AuthResponse<ReissueTokenResponseDto> reissue(@RequestBody @Valid ReissueTokenRequestDto request) {
        TokenPair tokenPair = tokenService.reissue(request.refreshToken());
        return AuthResponse.success(ReissueTokenResponseDto.from(tokenPair));
    }

}
