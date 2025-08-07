package com.dataracy.modules.auth.application.port.in.cache;

public interface CacheRefreshTokenUseCase {
    void saveRefreshToken(String userId, String refreshToken);

    String getRefreshToken(String userId);

    void deleteRefreshToken(String userId);
}
