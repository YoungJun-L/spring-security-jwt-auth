package com.youngjun.auth.core.api.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.kotest.core.annotation.Tags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Tags(values = "acceptance")
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = objectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .setMessageConverters(converter)
                .build();
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    protected abstract Object initController();

}
