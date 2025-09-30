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
    
    // Adapter 상수 정의
    private static final String REDIS_RATE_LIMIT_ADAPTER = "RedisRateLimitAdapter";
    
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
        
        try {
            // 먼저 카운트를 증가시키고 결과를 확인 (원자적 연산)
            Long count = redisTemplate.opsForValue().increment(redisKey, 1);
            
            if (count == null) {
                // Redis 장애 시 안전하게 차단 (보수적 접근)
                LoggerFactory.redis().logWarning(REDIS_RATE_LIMIT_ADAPTER, 
                    String.format("Redis increment 결과가 null - IP: %s, 안전을 위해 차단", key));
                return false;
            }
            
            // 첫 번째 요청인 경우에만 TTL 설정
            if (count == 1) {
                redisTemplate.expire(redisKey, windowMinutes, TimeUnit.MINUTES);
            }
            
            boolean allowed = count <= maxRequests;
            
            if (allowed) {
                LoggerFactory.redis().logInfo(REDIS_RATE_LIMIT_ADAPTER, 
                    String.format("요청 허용 - IP: %s, 현재 카운트: %d/%d", key, count, maxRequests));
            } else {
                LoggerFactory.redis().logWarning(REDIS_RATE_LIMIT_ADAPTER, 
                    String.format("요청 차단 - IP: %s, 현재 카운트: %d/%d", key, count, maxRequests));
            }
            
            return allowed;
            
        } catch (Exception e) {
            // Redis 장애 시 안전을 위해 차단
            LoggerFactory.redis().logError(REDIS_RATE_LIMIT_ADAPTER, 
                String.format("Redis 카운트 확인 실패 - IP: %s, 안전을 위해 차단", key), e);
            return false;
        }
    }
    
    @Override
    public void incrementRequestCount(String key, int incrementBy) {
        // isAllowed에서 이미 increment를 수행하므로 이 메서드는 더 이상 사용되지 않음
        // 하지만 인터페이스 구현을 위해 유지
        if (key == null || key.trim().isEmpty()) {
            return;
        }
        
        String redisKey = "rate_limit:" + key;
        
        try {
            Long count = redisTemplate.opsForValue().increment(redisKey, incrementBy);
            
            if (count == null) {
                LoggerFactory.redis().logWarning(REDIS_RATE_LIMIT_ADAPTER, 
                    String.format("Redis increment 결과가 null - IP: %s", key));
                return;
            }
            
            // 첫 번째 요청인 경우에만 TTL 설정
            if (count == incrementBy) {
                redisTemplate.expire(redisKey, defaultWindowMinutes, TimeUnit.MINUTES);
            }
            
            LoggerFactory.redis().logInfo(REDIS_RATE_LIMIT_ADAPTER, 
                String.format("요청 카운트 증가 - IP: %s, 현재 카운트: %d", key, count));
                
        } catch (Exception e) {
            LoggerFactory.redis().logError(REDIS_RATE_LIMIT_ADAPTER, 
                String.format("Redis 카운트 증가 실패 - IP: %s, 에러: %s", key, e.getMessage()), e);
        }
    }
}