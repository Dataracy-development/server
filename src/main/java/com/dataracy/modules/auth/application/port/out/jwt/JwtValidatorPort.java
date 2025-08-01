package com.dataracy.modules.auth.application.port.out.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

/**
 * 토큰 유효성 검사 및 정보 추출 포트
 */
public interface JwtValidatorPort {
    void validateToken (String token);

    Long getUserIdFromToken(String token);
    RoleType getRoleFromToken(String token);

    String getProviderFromRegisterToken(String token);
    String getProviderIdFromRegisterToken(String token);
    String getEmailFromRegisterToken(String token);
}
