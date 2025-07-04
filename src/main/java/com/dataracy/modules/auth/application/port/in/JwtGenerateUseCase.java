package com.dataracy.modules.auth.application.port.in;

import com.dataracy.modules.user.domain.enums.RoleType;

/**
 * 토큰 발급 유스케이스
 */
public interface JwtGenerateUseCase {
    String generateRegisterToken(String provider, String providerId, String email);
    String generateAccessToken(Long userId, RoleType role);
    String generateRefreshToken(Long userId, RoleType role);
}
