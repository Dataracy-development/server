package com.dataracy.modules.auth.application.port.in.cache;

public interface CacheResetTokenUseCase {
    void saveResetToken(String token);
    boolean isValidResetToken(String token);
}
