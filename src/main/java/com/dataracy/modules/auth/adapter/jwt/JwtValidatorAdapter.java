package com.dataracy.modules.auth.adapter.jwt;

import com.dataracy.modules.auth.application.port.out.jwt.JwtValidatorPort;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Jwt 토큰 유효성 검증 로직
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidatorAdapter implements JwtValidatorPort {
    private final JwtUtilInternal jwtUtilInternal;

    /**
     * 토큰의 유효성 검사
     *
     * @param token 유효성 확인을 하고자 하는 토큰
     */
    @Override
    public void validateToken (String token) {
        jwtUtilInternal.parseToken(token);
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출(어세스 토큰, 리프레시 토큰)
     *
     * @param token 토큰 문자열
     * @return 유저 아이디
     */
    @Override
    public Long getUserIdFromToken(String token) {
        return jwtUtilInternal.parseToken(token).get("userId", Long.class);
    }

    /**
     * Register Token에서 OAuth2 제공자 추출
     *
     * @param token Register Token 문자열
     * @return OAuth2 제공자
     */
    @Override
    public String getProviderFromRegisterToken(String token) {
        return jwtUtilInternal.parseToken(token).get("provider", String.class);
    }

    /**
     * Register Token에서 제공자 ID 추출
     *
     * @param token Register Token 문자열
     * @return OAuth2 제공자 ID
     */
    @Override
    public String getProviderIdFromRegisterToken(String token) {
        return jwtUtilInternal.parseToken(token).get("providerId", String.class);
    }

    /**
     * Register Token에서 이메일 추출
     *
     * @param token Register Token 문자열
     * @return 이메일
     */
    @Override
    public String getEmailFromRegisterToken(String token) {
        return jwtUtilInternal.parseToken(token).get("email", String.class);
    }

    /**
     * Token에서 Role 추출
     *
     * @param token Access Token 문자열
     * @return 사용자 Role(String)
     */
    @Override
    public RoleType getRoleFromToken(String token) {
        String role = jwtUtilInternal.parseToken(token).get("role", String.class);
        return RoleType.of(role);
    }

    @Override
    public String getEmailFromResetToken(String token) {
        return jwtUtilInternal.parseToken(token).get("email", String.class);
    }
}
