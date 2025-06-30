package com.dataracy.modules.auth.application;

import com.dataracy.modules.auth.infra.jwt.JwtProperties;
import com.dataracy.modules.auth.infra.jwt.JwtUtil;
import com.dataracy.modules.user.domain.converter.RoleStatusTypeConverter;
import com.dataracy.modules.user.domain.enums.RoleStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtQueryService {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    /**
     * JWT 토큰 유효성 검사.
     *
     * @param token 토큰 문자열
     */
    public void validateToken(String token) {
        jwtUtil.parseToken(token);
    }

    /**
     * JWT 토큰에서 사용자 ID 추출.
     *
     * @param token 토큰 문자열
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        return jwtUtil.parseToken(token).get("userId", Long.class);
    }

    /**
     * Register Token에서 OAuth2 제공자 추출.
     *
     * @param token Register Token 문자열
     * @return OAuth2 제공자
     */
    public String getProviderFromRegisterToken(String token) {
        return jwtUtil.parseToken(token).get("provider", String.class);
    }

    /**
     * Register Token에서 제공자 ID 추출.
     *
     * @param token Register Token 문자열
     * @return 제공자 ID
     */
    public String getProviderIdFromRegisterToken(String token) {
        return jwtUtil.parseToken(token).get("providerId", String.class);
    }

    /**
     * Register Token에서 이메일 추출.
     *
     * @param token Register Token 문자열
     * @return 사용자 이메일
     */
    public String getEmailFromRegisterToken(String token) {
        return jwtUtil.parseToken(token).get("email", String.class);
    }

    /**
     * Token에서 Role 추출.
     *
     * @param token Access Token 문자열
     * @return 사용자 Role
     */
    public RoleStatusType getRoleFromToken(String token) {
        String roleName = jwtUtil.parseToken(token).get("role", String.class);
        return RoleStatusTypeConverter.of(roleName);
    }

    /**
     * Access Token 유효기간 반환.
     *
     * @return Access Token 유효기간 (밀리초)
     */
    public long getAccessTokenExpirationTime() {
        return jwtProperties.getAccessTokenExpirationTime();
    }

    /**
     * Refresh Token 유효기간 반환.
     *
     * @return Refresh Token 유효기간 (밀리초)
     */
    public long getRefreshTokenExpirationTime() {
        return jwtProperties.getRefreshTokenExpirationTime();
    }

    /**
     * Register Token 유효기간 반환.
     *
     * @return Register Token 유효기간 (밀리초)
     */
    public long getRegisterTokenExpirationTime() {
        return jwtProperties.getRegisterTokenExpirationTime();
    }

    /**
     * 온보딩 리다이렉트 URL 반환.
     *
     * @return 온보딩 리다이렉트 URL
     */
    public String getRedirectOnboardingUrl() {
        return jwtProperties.getRedirectOnboarding();
    }

    /**
     * 로그인 성공 후 리다이렉트 URL 반환.
     *
     * @return 로그인 성공 후 리다이렉트 URL
     */
    public String getRedirectBaseUrl() {
        return jwtProperties.getRedirectBase();
    }
}
