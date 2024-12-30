package com.youngjun.auth.core.api.controller.v1;

import com.youngjun.auth.core.api.controller.v1.request.ReissueTokenRequestDto;
import com.youngjun.auth.core.api.support.RestDocsTest;
import com.youngjun.auth.core.api.support.RestDocsUtils;
import com.youngjun.auth.core.domain.token.TokenPair;
import com.youngjun.auth.core.domain.token.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

class TokenControllerTest extends RestDocsTest {

    private static final String ACCESS_TOKEN = "${ACCESS_TOKEN}";

    private static final Long ACCESS_TOKEN_EXPIRES_IN = 1L;

    private static final String REFRESH_TOKEN = "${REFRESH_TOKEN}";

    private static final Long REFRESH_TOKEN_EXPIRES_IN = 1L;

    private final TokenService tokenService = Mockito.mock(TokenService.class);

    @Override
    protected Object initController() {
        return new TokenController(tokenService);
    }

    @DisplayName("재발급 성공")
    @Test
    void reissue() throws Exception {
        ReissueTokenRequestDto request = new ReissueTokenRequestDto("${REFRESH_TOKEN}");

        BDDMockito.given(tokenService.reissue(ArgumentMatchers.anyString())).willReturn(buildTokenPair());

        mockMvc
                .perform(MockMvcRequestBuilders.post("/auth/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document("token", RestDocsUtils.requestPreprocessor(),
                        RestDocsUtils.responsePreprocessor(),
                        PayloadDocumentation.requestFields(PayloadDocumentation.fieldWithPath("refreshToken")
                                .type(JsonFieldType.STRING)
                                .description("refreshToken")),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("status")
                                        .type(JsonFieldType.STRING)
                                        .description("status"),
                                PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT).description("data"),
                                PayloadDocumentation.fieldWithPath("data.accessToken")
                                        .type(JsonFieldType.STRING)
                                        .description("accessToken"),
                                PayloadDocumentation.fieldWithPath("data.accessTokenExpiresIn")
                                        .type(JsonFieldType.NUMBER)
                                        .description("accessToken 만료 시간, UNIX 타임스탬프(Timestamp)"),
                                PayloadDocumentation.fieldWithPath("data.refreshToken")
                                        .type(JsonFieldType.STRING)
                                        .description("refreshToken"),
                                PayloadDocumentation.fieldWithPath("data.refreshTokenExpiresIn")
                                        .type(JsonFieldType.NUMBER)
                                        .description("refreshToken 만료 시간, UNIX 타임스탬프(Timestamp)"),
                                PayloadDocumentation.fieldWithPath("error")
                                        .type(JsonFieldType.NULL)
                                        .description("error")
                                        .ignored())));
    }

    @DisplayName("유효하지 않은 refresh token 으로 재발급 시 실패한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", // Empty String
            " ", // Only whitespace
    })
    void reissueWithInvalidRefreshToken(String invalidRefreshToken) throws Exception {
        ReissueTokenRequestDto request = new ReissueTokenRequestDto(invalidRefreshToken);

        BDDMockito.given(tokenService.reissue(ArgumentMatchers.anyString())).willReturn(null);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/auth/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private TokenPair buildTokenPair() {
        return new TokenPair(1L, ACCESS_TOKEN, ACCESS_TOKEN_EXPIRES_IN, REFRESH_TOKEN, REFRESH_TOKEN_EXPIRES_IN);
    }

}
