package com.youngjun.auth.core.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngjun.auth.core.api.security.config.SecurityTestConfig;
import com.youngjun.auth.core.domain.auth.AuthService;
import com.youngjun.auth.core.domain.token.TokenParser;
import com.youngjun.auth.core.domain.token.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Tag("restdocs")
@ExtendWith(RestDocumentationExtension.class)
@SpringJUnitWebConfig(SecurityTestConfig.class)
public abstract class SecurityTest {

    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected TokenService tokenService;

    @Autowired
    protected TokenParser tokenParser;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider, WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authService, tokenService, tokenParser);
    }

}
