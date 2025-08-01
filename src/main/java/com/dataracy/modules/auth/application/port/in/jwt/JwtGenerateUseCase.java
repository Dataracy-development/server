package com.dataracy.modules.auth.application.port.in.jwt;

import com.dataracy.modules.user.domain.enums.RoleType;

public interface JwtGenerateUseCase {
    // 토큰 발급
    String generateRegisterToken(String provider, String providerId, String email);
    String generateAccessToken(Long userId, RoleType role);
    String generateRefreshToken(Long userId, RoleType role);
}
