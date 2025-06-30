package com.dataracy.modules.auth.application;

import com.dataracy.modules.auth.infra.redis.TokenRedisManager;
import com.dataracy.modules.common.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenApplicationService {

    private final TokenRedisManager tokenRedisManager;

    /**
     * 분산 락 기반으로 리프레시 토큰을 저장합니다.
     *
     * @param userId       사용자 ID
     * @param refreshToken 리프레시 토큰
     */
    @DistributedLock(key = "'lock:refresh:' + #userId", waitTime = 200, leaseTime = 3000)
    public void saveRefreshToken(String userId, String refreshToken) {
        String existing = tokenRedisManager.getStoredRefreshToken(userId);
        if (refreshToken.equals(existing)) {
            log.debug("[TOKEN] 동일한 토큰이 이미 존재함 - 저장 생략");
            return;
        }
        tokenRedisManager.saveRefreshToken(userId, refreshToken);
    }

    /**
     * 리프레시 토큰 검증 및 사용자 ID 추출.
     *
     * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰
     * @return 사용자 ID
     */
    public Long validateAndExtractUserId(String refreshToken) {
        return tokenRedisManager.validateAndExtractUserId(refreshToken);
    }

    /**
     * 해당 토큰을 블랙리스트 처리한다.
     * @param token 토큰
     * @param expirationMillis 밀리초
     */
    public void blacklistToken(String token, long expirationMillis) {
        tokenRedisManager.blacklistToken(token, expirationMillis);
    }

    /**
     * 해당 토큰이 블랙리스트 처리되었는지 여부를 파악한다.
     * @param token 어세스토큰
     * @return 블랙리스트 처리 여부
     */
    public boolean isBlacklisted(String token) {
        return tokenRedisManager.isBlacklisted(token);
    }
}
