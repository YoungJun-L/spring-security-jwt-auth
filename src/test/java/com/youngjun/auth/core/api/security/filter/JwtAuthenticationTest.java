package com.youngjun.auth.core.api.security.filter;

import com.youngjun.auth.core.api.security.SecurityTest;
import com.youngjun.auth.core.api.support.RestDocsUtils;
import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.auth.AuthStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class JwtAuthenticationTest extends SecurityTest {

    @DisplayName("JWT 인증 성공")
    @Test
    void jwtAuthenticate() throws Exception {
        String accessToken = "accessToken";
        Auth auth = new Auth(1L, "username", "password", AuthStatus.ENABLED);
        BDDMockito.given(tokenParser.parseSubject(accessToken)).willReturn("username");
        BDDMockito.given(authService.loadUserByUsername("username")).willReturn(auth);

        mockMvc.perform(MockMvcRequestBuilders.get("/test").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document("authenticate", RestDocsUtils.requestPreprocessor(),
                        HeaderDocumentation.requestHeaders(HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION)
                                .description("JWT access token")
                                .optional())));
    }

    @DisplayName("JWT 인증 실패")
    @Test
    void jwtAuthenticateFailed() throws Exception {
        String token = "token";
        BDDMockito.given(tokenParser.parseSubject(token)).willThrow(new BadTokenException("error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/test").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}
