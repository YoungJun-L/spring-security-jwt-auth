package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.controller.v1.request.ReissueTokenRequest
import com.youngjun.auth.core.api.support.RestDocsTest
import com.youngjun.auth.core.api.support.description
import com.youngjun.auth.core.api.support.ignored
import com.youngjun.auth.core.api.support.optional
import com.youngjun.auth.core.api.support.type
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenPairBuilder
import io.mockk.every
import io.mockk.mockk
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

class TokenControllerTest : RestDocsTest() {
    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setUp() {
        tokenService = mockk()
        val controller = TokenController(tokenService)
        setMockMvc(controller)
    }

    @Test
    fun `재발급 성공`() {
        val value = "refreshToken"
        every { tokenService.reissue(RefreshToken(value)) } returns TokenPairBuilder().build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(ReissueTokenRequest(value))
            .post("/auth/token")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "token",
                    requestFields(
                        "refreshToken" type STRING description "refreshToken",
                    ),
                    responseFields(
                        "status" type STRING description "status",
                        "data" type OBJECT description "data",
                        "data.accessToken" type STRING description "accessToken",
                        "data.accessTokenExpiresIn" type NUMBER description "accessToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                        "data.refreshToken" type STRING description "refreshToken" optional true,
                        "data.refreshTokenExpiresIn" type NUMBER description "refreshToken 만료 시간, UNIX 타임스탬프(Timestamp)" optional true,
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }
}
