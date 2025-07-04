package com.dataracy.modules.auth.infra.jwt;

import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import com.dataracy.modules.user.domain.converter.RoleStatusTypeConverter;
import com.dataracy.modules.user.domain.enums.RoleStatusType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
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

    @Value("${spring.jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
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

    /**
     * JWT 토큰 유효성 검사.
     *
     * @param token 토큰 문자열
     */
    public void validateToken(String token) {
        parseToken(token);
    }

    /**
     * JWT 토큰에서 사용자 ID 추출.
     *
     * @param token 토큰 문자열
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * Register Token에서 OAuth2 제공자 추출.
     *
     * @param token Register Token 문자열
     * @return OAuth2 제공자
     */
    public String getProviderFromRegisterToken(String token) {
        return parseToken(token).get("provider", String.class);
    }

    /**
     * Register Token에서 제공자 ID 추출.
     *
     * @param token Register Token 문자열
     * @return 제공자 ID
     */
    public String getProviderIdFromRegisterToken(String token) {
        return parseToken(token).get("providerId", String.class);
    }

    /**
     * Register Token에서 이메일 추출.
     *
     * @param token Register Token 문자열
     * @return 사용자 이메일
     */
    public String getEmailFromRegisterToken(String token) {
        return parseToken(token).get("email", String.class);
    }

    /**
     * Token에서 Role 추출.
     *
     * @param token Access Token 문자열
     * @return 사용자 Role
     */
    public RoleStatusType getRoleFromToken(String token) {
        String roleName = parseToken(token).get("role", String.class);
        return RoleStatusTypeConverter.of(roleName);
    }
}
