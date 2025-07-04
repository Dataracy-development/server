package com.dataracy.modules.auth.adapter.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlackListRedisAdapter {
    private final StringRedisTemplate redisTemplate;

    /**
     * 블랙리스트 키 값을 설정
     *
     * @param token 토큰
     * @return 블랙리스트 키
     */
    private String getBlackListKey(String token) {
        return "blacklist:" + token;
    }

    /**
     * 해당 토큰을 블랙리스트 처리
     *
     * @param token jwt 토큰
     * @param expirationMillis 유효기간
     */
    public void setBlackListToken(
            String token,
            long expirationMillis
    ) {
        redisTemplate.opsForValue().set(
                getBlackListKey(token),
                "logout",
                Duration.ofMillis(expirationMillis)
        );
    }

    /**
     * 해당 토큰이 블랙리스트 처리되었는지 여부를 파악
     *
     * @param token 토큰
     * @return 블랙리스트 처리 여부
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getBlackListKey(token)));
    }
}
