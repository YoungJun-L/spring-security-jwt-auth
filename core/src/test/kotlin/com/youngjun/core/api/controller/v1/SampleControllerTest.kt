package com.youngjun.core.api.controller.v1

import com.youngjun.core.application.SampleService
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
import org.springframework.restdocs.payload.JsonFieldType.ARRAY
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
    fun readSample() {
        every { sampleService.readSample(any(), any()) } returns Sample(1L, "data1")

        given()
            .log()
            .all()
            .cookie(userCookie)
            .contentType(ContentType.JSON)
            .pathParam("sampleId", 1)
            .get("/samples/{sampleId}")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "sample",
                    requestCookies(cookieWithName("USER_ID").description("userId")),
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

    @Test
    fun readSamples() {
        every { sampleService.readSamples(any()) } returns listOf(Sample(1L, "data1"), Sample(1L, "data2"))

        given()
            .log()
            .all()
            .cookie(userCookie)
            .contentType(ContentType.JSON)
            .get("/samples")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "sample",
                    requestCookies(cookieWithName("USER_ID").description("userId")),
                    responseFields(
                        "status" type STRING description "status",
                        "data" type OBJECT description "data",
                        "data.userId" type NUMBER description "userId",
                        "data.samples" type ARRAY description "samples",
                        "data.samples[].id" type NUMBER description "sample: id",
                        "data.samples[].userId" type NUMBER description "sample: userId",
                        "data.samples[].data" type STRING description "sample: data",
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }
}
