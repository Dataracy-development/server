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

    @Around("execution(* com.dataracy.modules..adapter.persistence.repositoryImpl..*(..))")
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
