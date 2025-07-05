package com.dataracy.modules.auth.application.port.in;

/**
 * 리프레시 토큰 레디스 유스케이스
 */
public interface TokenRedisUseCase {
    void saveRefreshToken(String userId, String refreshToken);
    String getRefreshToken(String userId);
    void deleteRefreshToken(String userId);
}
