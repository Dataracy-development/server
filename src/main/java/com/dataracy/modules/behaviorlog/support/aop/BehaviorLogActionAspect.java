package com.dataracy.modules.behaviorlog.support.aop;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.support.annotation.TrackClick;
import com.dataracy.modules.behaviorlog.support.annotation.TrackNavigation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class BehaviorLogActionAspect {

    @Before("@annotation(trackClick)")
    public void trackClick(JoinPoint joinPoint, TrackClick trackClick) {
        String actionValue = trackClick.value();
        MDC.put("action", actionValue.isEmpty() ? ActionType.CLICK.name() : actionValue);
    }

    @Before("@annotation(trackNavigation)")
    public void trackNavigation(JoinPoint joinPoint, TrackNavigation trackNavigation) {
        String actionValue = trackNavigation.value();
        MDC.put("action", actionValue.isEmpty() ? ActionType.NAVIGATION.name() : actionValue);
    }
}
