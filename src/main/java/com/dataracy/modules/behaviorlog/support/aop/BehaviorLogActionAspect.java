package com.dataracy.modules.behaviorlog.support.aop;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.support.annotation.TrackClick;
import com.dataracy.modules.behaviorlog.support.annotation.TrackNavigation;
import com.dataracy.modules.behaviorlog.support.mdc.MdcKey;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BehaviorLogActionAspect {
    private final StringRedisTemplate redisTemplate;

    @Before("@annotation(trackClick)")
    public void trackClick(JoinPoint joinPoint, TrackClick trackClick) {
        String actionValue = trackClick.value();
        MDC.put(MdcKey.ACTION, actionValue.isEmpty() ? ActionType.CLICK.name() : actionValue);
    }

    @Before("@annotation(trackNavigation)")
    public void trackNavigation(JoinPoint joinPoint, TrackNavigation trackNavigation) {
        String actionValue = trackNavigation.value();
        MDC.put(MdcKey.ACTION, actionValue.isEmpty() ? ActionType.NAVIGATION.name() : actionValue);

        HttpServletRequest request = getCurrentRequest();
        String anonymousId = MDC.get("anonymousId");
        String sessionId = MDC.get("sessionId");
        String path = request.getRequestURI();

        long now = System.currentTimeMillis();
        long start = System.nanoTime();

        String redisKey = "behavior:last:" + (anonymousId != null ? anonymousId : sessionId);
        String redisValue = redisTemplate.opsForValue().get(redisKey);

        // 기본값
        String lastPath = null;
        long lastTime = 0L;
        Long stayTime = null;

        if (redisValue != null && redisValue.contains(",")) {
            String[] split = redisValue.split(",");
            lastPath = split[0];
            lastTime = Long.parseLong(split[1]);
            stayTime = now - lastTime;
        }

        // Redis에 현재 경로 + 시간 저장
        redisTemplate.opsForValue().set(redisKey, path + "," + now, Duration.ofMinutes(10));

        MDC.put(MdcKey.REFERRER, lastPath);
        MDC.put(MdcKey.STAY_TIME, String.valueOf(stayTime));
        MDC.put(MdcKey.NEXT_PATH, path);
    }

    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }
}