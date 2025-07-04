package com.dataracy.modules.auth.application.port.out;

/**
 * 리프레시 토큰 레디스 저장 포트
 */
public interface TokenRedisPort {
    void saveRefreshToken(String userId, String refreshToken);
    String getRefreshToken(String userId);
    void deleteRefreshToken(String userId);
}
