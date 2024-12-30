package com.youngjun.auth.core.api.controller.v1;

import com.youngjun.auth.core.api.controller.v1.request.RegisterAuthRequestDto;
import com.youngjun.auth.core.api.support.RestDocsTest;
import com.youngjun.auth.core.api.support.RestDocsUtils;
import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.auth.AuthService;
import com.youngjun.auth.core.domain.auth.AuthStatus;
import com.youngjun.auth.core.domain.auth.NewAuth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

class AuthControllerTest extends RestDocsTest {

    private static final String VALID_USERNAME = "username123";

    private static final String VALID_PASSWORD = "password123!";

    private final AuthService authService = Mockito.mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }

    @DisplayName("회원가입 성공")
    @Test
    void register() throws Exception {
        RegisterAuthRequestDto request = new RegisterAuthRequestDto(VALID_USERNAME, VALID_PASSWORD);

        BDDMockito.given(authService.register(ArgumentMatchers.any(NewAuth.class)))
                .willReturn(new Auth(1L, "username", "password", AuthStatus.ENABLED));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/auth/register")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document("register", RestDocsUtils.requestPreprocessor(),
                        RestDocsUtils.responsePreprocessor(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("username")
                                        .type(JsonFieldType.STRING)
                                        .description("username, 최소 8자 이상 최대 50자 미만의 1개 이상 영문자, 1개 이상 숫자 조합"),
                                PayloadDocumentation.fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("password, 최소 10자 이상 최대 50자 미만의 1개 이상 영문자, 1개 이상 특수문자, 1개 이상 숫자 조합")),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("status")
                                        .type(JsonFieldType.STRING)
                                        .description("status"),
                                PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT).description("data"),
                                PayloadDocumentation.fieldWithPath("data.userId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("userId"),
                                PayloadDocumentation.fieldWithPath("error")
                                        .type(JsonFieldType.NULL)
                                        .description("error")
                                        .ignored())));
    }

    @DisplayName("유효하지 않은 아이디로 회원가입 시 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", // Empty String
            " ", // Only whitespace
            "abcd123", // Less than 8 characters
            "0123456789abcdefghijabcdefghijabcdefghijabcdefghij", // 50 characters
            "abcdefgh", // Only characters
            "01234567", // Only digits
            "abcdef 123", // Contain whitespace
    })
    void registerWithInvalidUsername(String invalidUsername) throws Exception {
        RegisterAuthRequestDto request = new RegisterAuthRequestDto(invalidUsername, VALID_PASSWORD);

        BDDMockito.given(authService.register(ArgumentMatchers.any(NewAuth.class))).willReturn(null);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/auth/register")
                        .param("username", "username")
                        .param("password", "password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("유효하지 않은 비밀번호로 회원가입 시 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", // Empty String
            " ", // Only whitespace
            "abcdef123", // Less than 10 characters
            "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghij", // 50 characters
            "abcdefgh", // Only characters
            "01234567", // Only digits
            "!@#$%^&*", // Only special characters
            "abcdef 123 !", // Contain whitespace
    })
    void registerWithInvalidPassword(String invalidPassword) throws Exception {
        RegisterAuthRequestDto request = new RegisterAuthRequestDto(VALID_USERNAME, invalidPassword);

        BDDMockito.given(authService.register(ArgumentMatchers.any(NewAuth.class))).willReturn(null);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/auth/register")
                        .param("username", "username")
                        .param("password", "password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
