package com.dataracy.modules.auth.adapter.ratelimit;

import com.dataracy.modules.auth.application.port.out.RateLimitPort;
import org.springframework.stereotype.Component;

/**
 * NoOp Rate Limiting 구현체
 * Rate Limiting을 비활성화할 때 사용
 */
@Component("noOpRateLimitAdapter")
public class NoOpRateLimitAdapter implements RateLimitPort {
    
    @Override
    public boolean isAllowed(String key, int maxRequests, int windowMinutes) {
        return true; // 항상 허용
    }
    
    @Override
    public void incrementRequestCount(String key, int incrementBy) {
        // 아무것도 하지 않음
    }
}
