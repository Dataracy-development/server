package com.dataracy.modules.auth.adapter.jwt;

import com.dataracy.modules.auth.domain.enums.TokenType;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtilInternal {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    /**
     * JWT 시크릿 키를 초기화하고 유효성을 검사합니다.
     *
     * 시크릿 키가 32자 미만일 경우 예외를 발생시킵니다.
     *
     * @throws AuthException 시크릿 키가 32자 미만인 경우 발생합니다.
     */
    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();

        // jwt secret은 최소 32자 이상 조건
        if (secret.length() < 32) {
            LoggerFactory.common().logError("JWT", "시크릿 키는 32자 이상만 가능합니다.");
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
     * 주어진 JWT 토큰 문자열을 파싱하여 클레임 정보를 반환합니다.
     *
     * 토큰의 서명과 유효성을 검증하며, 만료되었거나 유효하지 않은 경우 각각 `AuthException`이 발생합니다.
     * 기타 예외 상황에서는 `CommonException`이 발생할 수 있습니다.
     *
     * @param token 파싱할 JWT 토큰 문자열
     * @return 토큰에 포함된 클레임 정보
     */
    public Claims parseToken(String token) {
        try {
            // 토큰 파싱으로 클레임 객체 반환
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            LoggerFactory.common().logError("JWT", "만료된 토큰입니다.", e);
            throw new AuthException(AuthErrorStatus.EXPIRED_TOKEN);
        } catch (JwtException e) {
            LoggerFactory.common().logError("JWT", "유효하지 않은 토큰입니다.", e);
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        } catch (Exception e) {
            LoggerFactory.common().logError("JWT", "토큰을 파싱하는 과정에서 알 수 없는 에러가 발생했습닝다.", e);
            throw new CommonException(CommonErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
