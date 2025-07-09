package com.dataracy.modules.behaviorlog.support.aop;

import com.dataracy.modules.behaviorlog.domain.enums.ActionType;
import com.dataracy.modules.behaviorlog.support.annotation.TrackClick;
import com.dataracy.modules.behaviorlog.support.annotation.TrackNavigation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class BehaviorLogActionAspect {

    /**
     * 클릭 동작이 감지되었을 때, MDC에 ActionType을 주입
     */
    @Before("@annotation(trackClick)")
    public void beforeClick(TrackClick trackClick) {
        MDC.put("action", ActionType.CLICK.name());
        log.debug("[AOP] TrackClick 감지 → MDC.action = CLICK");
    }

    /**
     * 이동 동작이 감지되었을 때, MDC에 ActionType을 주입
     */
    @Before("@annotation(trackNavigation)")
    public void beforeNavigation(TrackNavigation trackNavigation) {
        MDC.put("action", ActionType.NAVIGATION.name());
        log.debug("[AOP] TrackNavigation 감지 → MDC.action = NAVIGATION");
    }
}
