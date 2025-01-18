package com.youngjun.core.api.controller.v1

import com.youngjun.core.api.application.SampleService
import com.youngjun.core.domain.Sample
import com.youngjun.core.support.RestDocsTest
import com.youngjun.core.support.description
import com.youngjun.core.support.ignored
import com.youngjun.core.support.type
import io.mockk.every
import io.mockk.mockk
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName
import org.springframework.restdocs.cookies.CookieDocumentation.requestCookies
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

class SampleControllerTest : RestDocsTest() {
    private lateinit var sampleService: SampleService

    @BeforeEach
    fun setUp() {
        sampleService = mockk()
        val sampleController = SampleController(sampleService)
        setMockMvc(sampleController)
    }

    @Test
    fun sample() {
        every { sampleService.read(any()) } returns Sample(1L, 1L, "data")

        given()
            .log()
            .all()
            .cookie(userCookie)
            .contentType(ContentType.JSON)
            .get("/sample")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "sample",
                    requestCookies(cookieWithName("user").description("Access token")),
                    responseFields(
                        "status" type STRING description "status",
                        "data" type OBJECT description "data",
                        "data.id" type NUMBER description "id",
                        "data.userId" type NUMBER description "userId",
                        "data.data" type STRING description "sample data",
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }
}
