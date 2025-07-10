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
public class ServiceLatencyAspect {

    @Around("execution(* com.dataracy.modules..service..*(..))")
    public Object trackServiceLatency(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long latency = end - start;
            MDC.put(MdcKey.DB_LATENCY, String.valueOf(latency));
            log.debug("[DB Latency] {} ms - {}", latency, joinPoint.getSignature());
        }
    }
}
