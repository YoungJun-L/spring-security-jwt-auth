package com.youngjun.auth.core.api.controller.v1.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequestDto(@NotBlank String refreshToken) {
}
