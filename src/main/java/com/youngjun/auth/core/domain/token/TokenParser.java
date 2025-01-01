package com.youngjun.auth.core.domain.token;

import com.youngjun.auth.api.support.error.AuthException;
import com.youngjun.auth.api.support.error.ErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
        } catch (ExpiredJwtException ex) {
            throw new AuthException(ErrorType.TOKEN_EXPIRED_ERROR, null);
        } catch (Exception ex) {
            throw new AuthException(ErrorType.TOKEN_INVALID_ERROR, null);
        }
    }

    public void verify(RefreshToken token) {
        try {
            jwtParser.parseSignedClaims(token.value());
        } catch (ExpiredJwtException ex) {
            throw new AuthException(ErrorType.TOKEN_EXPIRED_ERROR, null);
        } catch (Exception ex) {
            throw new AuthException(ErrorType.TOKEN_INVALID_ERROR, null);
        }
    }

}
