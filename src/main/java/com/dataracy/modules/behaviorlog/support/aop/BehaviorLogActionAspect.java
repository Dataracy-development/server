package com.dataracy.modules.behaviorlog.support.aop;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.support.annotation.TrackClick;
import com.dataracy.modules.behaviorlog.support.annotation.TrackNavigation;
import com.dataracy.modules.behaviorlog.support.mdc.MdcKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BehaviorLogActionAspect {

    private final StringRedisTemplate redisTemplate;

    @Before("@annotation(trackClick)")
    public void handleTrackClick(JoinPoint joinPoint, TrackClick trackClick) {
        String actionValue = trackClick.value();
        MDC.put(MdcKey.ACTION, actionValue.isEmpty() ? ActionType.CLICK.name() : actionValue);
    }

    @Before("@annotation(trackNavigation)")
    public void handleTrackNavigation(JoinPoint joinPoint, TrackNavigation trackNavigation) {
        String actionValue = trackNavigation.value();
        MDC.put(MdcKey.ACTION, actionValue.isEmpty() ? ActionType.NAVIGATION.name() : actionValue);

        String anonymousId = MDC.get(MdcKey.ANONYMOUS_ID);
        String sessionId = MDC.get(MdcKey.SESSION_ID);
        String path = MDC.get(MdcKey.PATH);

        long now = System.currentTimeMillis();
        String redisKey = buildRedisKey(anonymousId, sessionId);
        String redisValue = redisTemplate.opsForValue().get(redisKey);

        String lastPath = null;
        long lastTime = 0L;
        Long stayTime = null;

        if (redisValue != null && redisValue.contains(",")) {
            String[] parts = redisValue.split(",");
            if (parts.length >= 2) {
                lastPath = parts[0];
                try {
                    lastTime = Long.parseLong(parts[1]);
                    stayTime = now - lastTime;
                } catch (NumberFormatException e) {
                    log.warn("Invalid timestamp in Redis value: {}", parts[1]);
                }
            }
        }

        // Redis 업데이트
        redisTemplate.opsForValue().set(redisKey, path + "," + now, Duration.ofMinutes(10));

        MDC.put(MdcKey.REFERRER, lastPath);
        MDC.put(MdcKey.NEXT_PATH, path);
        if (stayTime != null) {
            MDC.put(MdcKey.STAY_TIME, String.valueOf(stayTime));
        }

        log.trace("AOP 탐지 - referrer={}, nextPath={}, stayTime={}", lastPath, path, stayTime);
    }

    private String buildRedisKey(String anonymousId, String sessionId) {
        return "behavior:last:" + (anonymousId != null ? anonymousId : sessionId);
    }
}
