package com.dataracy.modules.auth.adapter.jwt;

import com.dataracy.modules.auth.domain.enums.TokenType;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.common.exception.CommonException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtilInternal {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();

        // jwt secret은 최소 32자 이상 조건
        if (secret.length() < 32) {
            throw new AuthException(AuthErrorStatus.SHORT_JWT_SECRET);
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰 발급
     *
     * @param claims 토큰에 포함할 클레임
     * @param expirationMillis 토큰 유효기간 (밀리초)
     * @param tokenType 발급할 토큰의 유형
     * @return 생성된 토큰 문자열
     */
    public String generateToken(
            Map<String, Object> claims,
            long expirationMillis,
            TokenType tokenType
    ) {
        // 시간 설정
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        // Jwt 발행
        JwtBuilder builder = Jwts.builder()
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256);
        builder.claim("type", tokenType.name());
        claims.forEach(builder::claim);

        return builder.compact();
    }

    /**
     * JWT 토큰 파싱 및 유효성 검사
     *
     * @param token 파싱 하고자 하는 토큰 문자열
     * @return 토큰의 클레임 정보
     */
    public Claims parseToken(String token) {
        try {
            // 토큰 파싱으로 클레임 객체 반환
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims;
        } catch (ExpiredJwtException e) {
            log.error("Expired Token: {}", token, e);
            throw new AuthException(AuthErrorStatus.EXPIRED_TOKEN);
        } catch (JwtException e) {
            log.error("Invalid Token: {}", token, e);
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("Unknown error while parsing token: {}", token, e);
            throw new CommonException(CommonErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
