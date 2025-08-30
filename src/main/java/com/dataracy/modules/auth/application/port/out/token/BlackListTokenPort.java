package com.dataracy.modules.auth.application.port.out.token;

public interface BlackListTokenPort {
    void setBlackListToken(String token, long expirationMillis);
    boolean isBlacklisted(String token);
}
