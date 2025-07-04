package com.dataracy.modules.auth.application.port.in;

public interface TokenRedisUseCase {
    void saveRefreshToken(String userId, String refreshToken);
    String getRefreshToken(String userId);
    void deleteRefreshToken(String userId);
}
