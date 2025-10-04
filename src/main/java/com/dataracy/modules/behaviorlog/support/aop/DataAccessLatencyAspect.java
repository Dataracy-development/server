package com.dataracy.modules.behaviorlog.support.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.dataracy.modules.behaviorlog.support.mdc.MdcKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class DataAccessLatencyAspect {

  private static final int NANOS_TO_MILLIS_DIVISOR = 1_000_000;

  /****
   * 데이터 접근 관련 메서드의 실행 시간을 측정하여 MDC에 저장하고 디버그 로그로 기록합니다.
   *
   * 지정된 패키지(`com.dataracy.modules..adapter.*.impl..*`, `com.dataracy.modules..adapter.elasticsearch..*`, `com.dataracy.modules..adapter.query..*`) 내의 모든 메서드 실행 전후로 동작하며,
   * 실행 소요 시간을 밀리초 단위로 계산하여 MDC의 `DATA_ACCESS_LATENCY` 키에 저장하고, 디버그 로그로 남깁니다.
   *
   * @param joinPoint AOP가 감싼 대상 메서드의 실행 정보를 포함합니다.
   * @return 원래 대상 메서드의 반환값
   * @throws Throwable 대상 메서드 실행 중 발생한 예외를 그대로 전달합니다.
   */
  @Around(
      "execution(* com.dataracy.modules..adapter.*.impl..*(..))"
          + " || execution(* com.dataracy.modules..adapter.elasticsearch..*(..))"
          + " || execution(* com.dataracy.modules..adapter.query..*(..))")
  public Object trackDataAccessLatency(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.nanoTime();
    try {
      return joinPoint.proceed();
    } finally {
      long end = System.nanoTime();
      long latency = (end - start) / NANOS_TO_MILLIS_DIVISOR; // Convert to milliseconds
      MDC.put(MdcKey.DATA_ACCESS_LATENCY, String.valueOf(latency));
      log.debug("[DB Latency] {} ms - {}", latency, joinPoint.getSignature());
    }
  }
}
