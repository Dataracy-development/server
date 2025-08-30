package com.dataracy.modules.auth.application.port.in.token;

public interface BlackListTokenUseCase {
    void addToBlackList(String token, long expirationMillis);

    boolean isBlacklisted(String token);
}
