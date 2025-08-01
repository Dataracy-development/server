package com.dataracy.modules.auth.adapter.redis;

import com.dataracy.modules.common.logging.support.LoggerFactory;
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
        LoggerFactory.redis().logSaveOrUpdate(token, "블랙 리스트 처리를 위한 토큰 레디스 저장에 성공했습니다.");
    }

    /**
     * 해당 토큰이 블랙리스트 처리되었는지 여부를 파악
     *
     * @param token 토큰
     * @return 블랙리스트 처리 여부
     */
    public boolean isBlacklisted(String token) {
        boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey(getBlackListKey(token)));
        LoggerFactory.redis().logExist(token, "해당 토큰은 블랙 리스트 처리 된 토큰입니다.");
        return isBlacklisted;
    }
}
