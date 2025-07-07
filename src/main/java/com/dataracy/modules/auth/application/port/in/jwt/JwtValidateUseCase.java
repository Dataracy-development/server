package com.dataracy.modules.auth.application.port.in.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

/**
 * 토큰 검증 및 정보 추출 유스케이스
 */
public interface JwtValidateUseCase {
    void validateToken (String token);

    Long getUserIdFromToken(String token);
    String getProviderFromRegisterToken(String token);
    String getProviderIdFromRegisterToken(String token);
    String getEmailFromRegisterToken(String token);
    RoleType getRoleFromToken(String token);

    long getRegisterTokenExpirationTime();
    long getAccessTokenExpirationTime();
    long getRefreshTokenExpirationTime();

    String getRedirectOnboardingUrl();
    String getRedirectBaseUrl();
}
