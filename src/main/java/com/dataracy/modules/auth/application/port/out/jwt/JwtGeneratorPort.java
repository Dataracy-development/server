package com.dataracy.modules.auth.application.port.out.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

/**
 * 토큰 발급 포트
 */
public interface JwtGeneratorPort {
    String generateRegisterToken(String provider, String providerId, String email);
    String generateAccessToken(Long userId, RoleType role);
    String generateRefreshToken(Long userId, RoleType role);
}
