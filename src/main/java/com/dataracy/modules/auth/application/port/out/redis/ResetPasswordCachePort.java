package com.dataracy.modules.auth.application.port.out.redis;

public interface ResetPasswordCachePort {
    void saveResetToken(String token);
    boolean isValidResetToken(String token);
}
