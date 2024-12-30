package com.youngjun.auth.core.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngjun.auth.core.api.security.request.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class RequestBodyUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final AntPathRequestMatcher LOGIN_REQUEST_MATCHER = AntPathRequestMatcher.antMatcher(HttpMethod.POST,
			"/auth/login");

	private final ObjectMapper objectMapper;

	public RequestBodyUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
			AuthenticationSuccessHandler authenticationSuccessHandler,
			AuthenticationFailureHandler authenticationFailureHandler, ObjectMapper objectMapper) {
		super(LOGIN_REQUEST_MATCHER, authenticationManager);
		setAuthenticationSuccessHandler(authenticationSuccessHandler);
		setAuthenticationFailureHandler(authenticationFailureHandler);
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		LoginRequestDto loginRequestDto;
		try {
			loginRequestDto = objectMapper.readValue(request.getReader(), LoginRequestDto.class);
		}
		catch (Exception ex) {
			throw new BadCredentialsException(ex.getMessage(), ex);
		}
		UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken
			.unauthenticated(loginRequestDto.username(), loginRequestDto.password());
		return this.getAuthenticationManager().authenticate(authRequest);
	}

}
