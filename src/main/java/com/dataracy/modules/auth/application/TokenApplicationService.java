package com.dataracy.modules.auth.application;

import com.dataracy.modules.auth.infra.redis.TokenRedisManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenApplicationService {

    private final TokenRedisManager tokenRedisManager;
    private final JwtApplicationService jwtApplicationService;
    private final JwtQueryService jwtQueryService;

    /**
     * 리프레시 토큰 저장.
     *
     * @param userId       사용자 ID
     * @param refreshToken 리프레시 토큰
     */
    public void saveRefreshToken(String userId, String refreshToken) {
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
     * 로그아웃 시 해당 어세스토큰을 블랙리스트 처리한다.
     * @param token 토큰
     * @param expirationMillis 밀리초
     */
    public void blacklistAccessToken(String token, long expirationMillis) {
        tokenRedisManager.blacklistAccessToken(token, expirationMillis);
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
