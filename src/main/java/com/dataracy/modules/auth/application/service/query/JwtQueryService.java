package com.dataracy.modules.auth.application.service.query;

import com.dataracy.modules.auth.adapter.jwt.JwtProperties;
import com.dataracy.modules.auth.application.port.in.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.out.JwtValidatorPort;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtQueryService implements JwtValidateUseCase {
    private final JwtValidatorPort jwtValidatorPort;
    private final JwtProperties jwtProperties;

    /**
     * jwt 토큰 유효성 검사
     *
     * @param token 토큰 문자열
     */
    @Override
    public void validateToken(String token) {
        jwtValidatorPort.validateToken(token);
    }

    /**
     * JWT 토큰에서 유저 ID 추출
     *
     * @param token 토큰 문자열(어세스 토큰, 리프레시 토큰)
     * @return 유저 ID
     */
    @Override
    public Long getUserIdFromToken(String token) {
        return jwtValidatorPort.getUserIdFromToken(token);
    }

    /**
     * Register Token에서 OAuth2 제공자 추출
     *
     * @param token 토큰 문자열(리프레시 토큰)
     * @return OAuth2 제공자
     */
    @Override
    public String getProviderFromRegisterToken(String token) {
        return jwtValidatorPort.getProviderFromRegisterToken(token);
    }

    /**
     * Register Token에서 제공자 ID 추출
     *
     * @param token 토큰 문자열(리프레시 토큰)
     * @return 제공자 ID
     */
    @Override
    public String getProviderIdFromRegisterToken(String token) {
        return jwtValidatorPort.getProviderIdFromRegisterToken(token);
    }

    /**
     * Register Token에서 이메일 추출
     *
     * @param token 토큰 문자열(리프레시 토큰)
     * @return 이메일
     */
    @Override
    public String getEmailFromRegisterToken(String token) {
        return jwtValidatorPort.getEmailFromRegisterToken(token);
    }

    /**
     * Token에서 Role 추출
     *
     * @param token 토큰 문자열(어세스 토큰, 리프레시 토큰)
     * @return 유저 Role
     */
    @Override
    public RoleType getRoleFromToken(String token) {
        return jwtValidatorPort.getRoleFromToken(token);
    }

    /**
     * Register Token 유효기간 반환
     *
     * @return Register Token 유효기간 (밀리초)
     */
    @Override
    public long getRegisterTokenExpirationTime() {
        return jwtProperties.getRegisterTokenExpirationTime();
    }

    /**
     * Access Token 유효기간 반환
     *
     * @return Access Token 유효기간 (밀리초)
     */
    @Override
    public long getAccessTokenExpirationTime() {
        return jwtProperties.getAccessTokenExpirationTime();
    }

    /**
     * Refresh Token 유효기간 반환
     *
     * @return Refresh Token 유효기간 (밀리초)
     */
    @Override
    public long getRefreshTokenExpirationTime() {
        return jwtProperties.getRefreshTokenExpirationTime();
    }

    /**
     * 온보딩 리다이렉트 URL 반환
     *
     * @return 온보딩 리다이렉트 URL
     */
    @Override
    public String getRedirectOnboardingUrl() {
        return jwtProperties.getRedirectOnboarding();
    }

    /**
     * 로그인 성공 후 리다이렉트 URL 반환
     *
     * @return 로그인 성공 후 리다이렉트 URL
     */
    @Override
    public String getRedirectBaseUrl() {
        return jwtProperties.getRedirectBase();
    }
}
