package com.dataracy.modules.auth.infra.jwt;

import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰 생성.
     *
     * @param claims           토큰에 포함할 클레임
     * @param expirationMillis 토큰 유효기간 (밀리초)
     * @return 생성된 토큰 문자열
     */
    public String generateToken(Map<String, Object> claims, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        JwtBuilder builder = Jwts.builder()
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256);

        claims.forEach(builder::claim);
        return builder.compact();
    }


    /**
     * JWT 토큰 파싱 및 유효성 검사.
     *
     * @param token 토큰 문자열
     * @return 토큰의 클레임 정보
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("Expired Token: {}", token);
            throw new AuthException(AuthErrorStatus.EXPIRED_TOKEN);
        } catch (JwtException e) {
            log.error("Invalid Token: {}", token);
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("Unknown error while parsing token: {}", token);
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        }
    }
}
