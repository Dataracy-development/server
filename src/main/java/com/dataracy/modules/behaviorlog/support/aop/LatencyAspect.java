package com.dataracy.modules.behaviorlog.support.aop;

import com.dataracy.modules.behaviorlog.support.mdc.MdcKey;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LatencyAspect {

    /**
     * 데이터베이스 관련 메서드 실행 시간을 측정하여 로그로 기록하고, Mapped Diagnostic Context(MDC)에 저장합니다.
     *
     * 지정된 패키지(`com.dataracy.modules..adapter.persistence.repositoryImpl..`) 내의 모든 메서드 실행 전후로 동작하며,
     * 실행 소요 시간을 밀리초 단위로 계산하여 MDC의 `DB_LATENCY` 키에 저장하고, 디버그 로그로 남깁니다.
     *
     * @param joinPoint AOP가 감싼 대상 메서드의 실행 정보를 포함합니다.
     * @return 원래 대상 메서드의 반환값
     * @throws Throwable 대상 메서드 실행 중 발생한 예외를 그대로 전달합니다.
     */
    @Around("execution(* com.dataracy.modules..adapter.persistence.impl..*(..))")
    public Object trackDbLatency(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.nanoTime();
            long latency = (end - start) / 1_000_000; // Convert to milliseconds
            MDC.put(MdcKey.DB_LATENCY, String.valueOf(latency));
            log.debug("[DB Latency] {} ms - {}", latency, joinPoint.getSignature());
        }
    }
}
