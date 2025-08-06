package com.dataracy.modules.auth.application.port.out.cache;

/**
 * 리프레시 토큰 레디스 저장, 검증 포트
 */
public interface CacheRefreshTokenPort {
    void saveRefreshToken(String userId, String refreshToken);
    String getRefreshToken(String userId);
    void deleteRefreshToken(String userId);
}
