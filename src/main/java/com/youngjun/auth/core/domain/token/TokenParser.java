package com.youngjun.auth.core.domain.token;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.youngjun.auth.core.api.support.error.AuthErrorType;
import com.youngjun.auth.core.api.support.error.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenParser {

	private final JwtParser jwtParser;

	public TokenParser(@Value("${spring.security.jwt.secret-key}") String secretKey) {
		this.jwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build();
	}

	public String parseSubject(String token) {
		try {
			return jwtParser.parseSignedClaims(token).getPayload().getSubject();
		}
		catch (ExpiredJwtException ex) {
			throw new AuthException(AuthErrorType.TOKEN_EXPIRED_ERROR);
		}
		catch (Exception ex) {
			throw new AuthException(AuthErrorType.TOKEN_INVALID_ERROR);
		}
	}

	public void verify(String token) {
		try {
			jwtParser.parseSignedClaims(token);
		}
		catch (ExpiredJwtException ex) {
			throw new AuthException(AuthErrorType.TOKEN_EXPIRED_ERROR);
		}
		catch (Exception ex) {
			throw new AuthException(AuthErrorType.TOKEN_INVALID_ERROR);
		}
	}

}
