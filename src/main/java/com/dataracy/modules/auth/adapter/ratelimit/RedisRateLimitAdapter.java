package com.dataracy.modules.auth.adapter.ratelimit;

import com.dataracy.modules.auth.application.port.out.RateLimitPort;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 레이트 리미팅 구현체
 * 분산 환경에서 정확한 카운팅과 영속성을 제공합니다.
 */
@Component("redisRateLimitAdapter")
public class RedisRateLimitAdapter implements RateLimitPort {
    
    private final StringRedisTemplate redisTemplate;
    
    @Value("${rate-limit.redis.max-requests:10}")
    private int defaultMaxRequests;
    
    @Value("${rate-limit.redis.window-minutes:1}")
    private int defaultWindowMinutes;
    
    public RedisRateLimitAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public boolean isAllowed(String key, int maxRequests, int windowMinutes) {
        if (key == null || key.trim().isEmpty()) {
            return true; // IP가 없으면 허용
        }
        
        String redisKey = "rate_limit:" + key;
        String countStr = redisTemplate.opsForValue().get(redisKey);
        
        int currentCount = 0;
        if (countStr != null) {
            try {
                currentCount = Integer.parseInt(countStr);
            } catch (NumberFormatException e) {
                LoggerFactory.redis().logWarning("RedisRateLimitAdapter", 
                    String.format("Redis에서 잘못된 카운트 값: %s, IP: %s", countStr, key));
            }
        }
        
        boolean allowed = currentCount < maxRequests;
        
        if (allowed) {
            LoggerFactory.redis().logInfo("RedisRateLimitAdapter", 
                String.format("요청 허용 - IP: %s, 현재 카운트: %d/%d", key, currentCount, maxRequests));
        } else {
            LoggerFactory.redis().logWarning("RedisRateLimitAdapter", 
                String.format("요청 차단 - IP: %s, 현재 카운트: %d/%d", key, currentCount, maxRequests));
        }
        
        return allowed;
    }
    
    @Override
    public void incrementRequestCount(String key, int incrementBy) {
        if (key == null || key.trim().isEmpty()) {
            return; // IP가 없으면 카운트하지 않음
        }
        
        String redisKey = "rate_limit:" + key;
        
        try {
            // Redis에서 원자적 증가 연산 수행
            Long count = redisTemplate.opsForValue().increment(redisKey, incrementBy);
            
            // 첫 번째 요청인 경우에만 TTL 설정
            if (count == incrementBy) {
                redisTemplate.expire(redisKey, defaultWindowMinutes, TimeUnit.MINUTES);
            }
            
            LoggerFactory.redis().logInfo("RedisRateLimitAdapter", 
                String.format("요청 카운트 증가 - IP: %s, 현재 카운트: %d", key, count));
                
        } catch (Exception e) {
            LoggerFactory.redis().logError("RedisRateLimitAdapter", 
                String.format("Redis 카운트 증가 실패 - IP: %s, 에러: %s", key, e.getMessage()), e);
        }
    }
}