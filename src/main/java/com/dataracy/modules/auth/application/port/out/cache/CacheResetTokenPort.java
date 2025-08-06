package com.dataracy.modules.auth.application.port.out.cache;

public interface CacheResetTokenPort {
    void saveResetToken(String token);
    boolean isValidResetToken(String token);
}
