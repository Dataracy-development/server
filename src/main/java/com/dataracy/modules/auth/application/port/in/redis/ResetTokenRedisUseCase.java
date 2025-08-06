package com.dataracy.modules.auth.application.port.in.redis;

public interface ResetTokenRedisUseCase {
    void saveResetToken(String token);
    boolean isValidResetToken(String token);
}
