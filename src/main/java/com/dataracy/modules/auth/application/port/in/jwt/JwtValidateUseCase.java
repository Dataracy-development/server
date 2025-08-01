package com.dataracy.modules.auth.application.port.in.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

public interface JwtValidateUseCase {
    // 토큰 유효성 검사
    void validateToken (String token);

    // 토큰으로 부터 유저 정보 추출
    Long getUserIdFromToken(String token);
    RoleType getRoleFromToken(String token);

    // 리프레시 토큰에서 소셜 서버로부터 받은 유저 정보를 추출
    String getProviderFromRegisterToken(String token);
    String getProviderIdFromRegisterToken(String token);
    String getEmailFromRegisterToken(String token);

    // 토큰으로부터 토큰 유효기간 추출
    long getRegisterTokenExpirationTime();
    long getAccessTokenExpirationTime();
    long getRefreshTokenExpirationTime();

    // 온보딩, 메인 url 조회
    String getRedirectOnboardingUrl();
    String getRedirectBaseUrl();
}
