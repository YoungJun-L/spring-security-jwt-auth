package com.youngjun.auth.core.api.security.filter;

import com.youngjun.auth.core.api.security.SecurityTest;
import com.youngjun.auth.core.api.security.request.LoginRequestDto;
import com.youngjun.auth.core.api.support.RestDocsUtils;
import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.auth.AuthStatus;
import com.youngjun.auth.core.domain.token.TokenPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

class LoginTest extends SecurityTest {

    @DisplayName("로그인 성공")
    @Test
    void login() throws Exception {
        LoginRequestDto request = new LoginRequestDto("${USERNAME}", "${PASSWORD}");
        Auth auth = new Auth(1L, "${USERNAME}", "${PASSWORD}", AuthStatus.ENABLED);
        BDDMockito.given(authService.loadUserByUsername("${USERNAME}")).willReturn(auth);
        BDDMockito.given(tokenService.issue(auth))
                .willReturn(new TokenPair(auth.id(), "${ACCESS_TOKEN}", 1L, "${REFRESH_TOKEN}", 1L));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document("login", RestDocsUtils.requestPreprocessor(),
                        RestDocsUtils.responsePreprocessor(),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("username")
                                        .type(JsonFieldType.STRING)
                                        .description("username"),
                                PayloadDocumentation.fieldWithPath("password")
                                        .type(JsonFieldType.STRING)
                                        .description("password")),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("status")
                                        .type(JsonFieldType.STRING)
                                        .description("status"),
                                PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT).description("data"),
                                PayloadDocumentation.fieldWithPath("data.userId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("발급 userId"),
                                PayloadDocumentation.fieldWithPath("data.tokens")
                                        .type(JsonFieldType.OBJECT)
                                        .description("tokens"),
                                PayloadDocumentation.fieldWithPath("data.tokens.accessToken")
                                        .type(JsonFieldType.STRING)
                                        .description("accessToken"),
                                PayloadDocumentation.fieldWithPath("data.tokens.accessTokenExpiresIn")
                                        .type(JsonFieldType.NUMBER)
                                        .description("accessToken 만료 시간, UNIX 타임스탬프(Timestamp)"),
                                PayloadDocumentation.fieldWithPath("data.tokens.refreshToken")
                                        .type(JsonFieldType.STRING)
                                        .description("refreshToken"),
                                PayloadDocumentation.fieldWithPath("data.tokens.refreshTokenExpiresIn")
                                        .type(JsonFieldType.NUMBER)
                                        .description("refreshToken 만료 시간, UNIX 타임스탬프(Timestamp)"),
                                PayloadDocumentation.fieldWithPath("error")
                                        .type(JsonFieldType.NULL)
                                        .description("error")
                                        .ignored())));
    }

    @DisplayName("가입하지 않은 회원이 로그인 시 실패한다.")
    @Test
    void loginFailedWithNotRegisteredUser() throws Exception {
        LoginRequestDto request = new LoginRequestDto("username", "password");
        BDDMockito.given(authService.loadUserByUsername("username")).willThrow(new UsernameNotFoundException("error"));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @DisplayName("서비스 이용이 불가능한 회원이 로그인 시 실패한다.")
    @Test
    void loginFailedWithLockedUser() throws Exception {
        LoginRequestDto request = new LoginRequestDto("username", "password");
        Auth auth = new Auth(1L, "username", "password", AuthStatus.LOCKED);
        BDDMockito.given(authService.loadUserByUsername("username")).willReturn(auth);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}
