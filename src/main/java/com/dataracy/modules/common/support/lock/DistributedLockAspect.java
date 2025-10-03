/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.support.lock;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;

import io.lettuce.core.dynamic.support.ParameterNameDiscoverer;
import io.lettuce.core.dynamic.support.StandardReflectionParameterNameDiscoverer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * 분산락 AOP를 설계한다. DistributedLock 어노테이션을 통하여 분산락을 시행 Spel문법을 통하여 레디스 키를 설정하며, 시간과 시도 횟수를 설정할 수 있다.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {
  private final RedissonDistributedLockManager lockManager;
  private final SpelExpressionParser parser = new SpelExpressionParser();
  private final ParameterNameDiscoverer nameDiscoverer =
      new StandardReflectionParameterNameDiscoverer();

  /** DistributedLockAspect의 초기화를 수행한 후 로그를 기록합니다. */
  @PostConstruct
  public void init() {
    LoggerFactory.lock().logInfo("[AOP] DistributedLockAspect 초기화 완료");
  }

  /**
   * 분산 락이 적용된 메서드 실행을 가로채어 락을 획득한 후 원래 메서드를 실행합니다.
   *
   * <p>분산 락 키는 {@link DistributedLock} 어노테이션의 SpEL 표현식을 기반으로 생성됩니다. 락 획득에 실패하거나 내부 실행 중 예외가 발생하면
   * 런타임 예외로 래핑되어 던져집니다.
   *
   * @return 원래 메서드의 실행 결과
   */
  @Around("@annotation(lock)")
  public Object around(ProceedingJoinPoint joinPoint, DistributedLock lock) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    String key = generateLockKey(method, joinPoint.getArgs(), lock);

    LoggerFactory.lock().logInfo("[AOP] 분산 락 진입 - method: {} key: {}", method.getName(), key);

    try {
      return lockManager.execute(
          key,
          lock.waitTime(),
          lock.leaseTime(),
          lock.retry(),
          () -> {
            LoggerFactory.lock()
                .logInfo("[AOP] 분산 락 내부 실행 - method: {} key: {}", method.getName(), key);
            try {
              return proceedSafely(joinPoint);
            } catch (BusinessException | CommonException e) {
              // 비즈니스/공통 예외는 그대로 전파
              throw e;
            } catch (Throwable e) {
              // 기타 예외는 래핑하여 전파
              LoggerFactory.lock().logError("[AOP] 원본 메서드 실행 중 예외 - key: {}", key, e);
              throw new CommonException(CommonErrorStatus.DISTRIBUTED_LOCK_EXECUTION_FAILURE);
            }
          });
    } catch (BusinessException e) {
      LoggerFactory.lock().logWarn("[AOP] 비즈니스 예외 - key: {} message: {}", key, e.getMessage());
      throw e;
    } catch (CommonException e) {
      LoggerFactory.lock().logError("[AOP] 공통 예외 - key: {} message: {}", key, e.getMessage());
      throw e;
    } catch (Throwable e) {
      LoggerFactory.lock().logError("[AOP] 락 내부 로직 예외 - key: {}", key, e);
      throw new CommonException(CommonErrorStatus.DISTRIBUTED_LOCK_EXECUTION_FAILURE);
    }
  }

  /**
   * AOP 조인포인트에서 원본 메서드를 안전하게 실행합니다.
   *
   * @param joinPoint AOP에서 전달된 실행 지점 정보
   * @return 원본 메서드의 실행 결과
   * @throws Throwable 원본 메서드 실행 중 발생한 예외를 그대로 전달
   */
  private Object proceedSafely(ProceedingJoinPoint joinPoint) throws Throwable {
    return joinPoint.proceed();
  }

  /**
   * 메서드와 인자, 그리고 DistributedLock 어노테이션의 SpEL 표현식을 이용해 분산 락 키를 생성합니다.
   *
   * @param method 분산 락이 적용된 대상 메서드
   * @param args 메서드의 인자 값 배열
   * @param lock DistributedLock 어노테이션 인스턴스
   * @return 생성된 분산 락 키 문자열
   * @throws LockAcquisitionException SpEL 파싱 실패 또는 생성된 키가 null/빈 문자열인 경우 발생
   */
  private String generateLockKey(Method method, Object[] args, DistributedLock lock) {
    String[] paramNames = nameDiscoverer.getParameterNames(method);
    EvaluationContext context = new StandardEvaluationContext();

    if (paramNames != null) {
      for (int i = 0; i < paramNames.length; i++) {
        context.setVariable(paramNames[i], args[i]);
      }
    }

    try {
      String key = parser.parseExpression(lock.key()).getValue(context, String.class);
      if (key == null || key.isBlank()) {
        throw new LockAcquisitionException("SpEL로 생성된 락 키가 null 또는 빈 문자열입니다.");
      }
      return key;
    } catch (SpelEvaluationException e) {
      LoggerFactory.lock().logError("[AOP] SpEL 키 파싱 오류 - expression: {}", lock.key(), e);
      throw new LockAcquisitionException("분산 락 키 SpEL 파싱 실패: " + lock.key(), e);
    }
  }
}
