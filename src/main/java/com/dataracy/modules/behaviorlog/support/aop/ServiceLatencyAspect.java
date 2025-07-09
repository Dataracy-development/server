package com.dataracy.modules.behaviorlog.support.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

@Slf4j
@Aspect
@Component
public class ServiceLatencyAspect {

    @Around("execution(* com.dataracy.modules..service..*(..))")
    public Object trackServiceLatency(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long latency = end - start;
            MDC.put("dbLatency", String.valueOf(latency));
            log.debug("[Latency] {} ms - {}", latency, joinPoint.getSignature());
        }
    }
}

