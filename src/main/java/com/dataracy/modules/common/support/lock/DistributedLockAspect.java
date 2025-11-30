package com.dataracy.modules.common.support.lock;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

/**
 * @DistributedLock 어노테이션을 처리하는 AOP Aspect
 *
 * <p>Redisson을 사용하여 분산 락을 획득하고 해제합니다.
 * SpEL 표현식을 사용하여 락 키를 동적으로 생성합니다.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Order(1) // 트랜잭션보다 먼저 실행되도록 설정
public class DistributedLockAspect {
  private final RedissonClient redissonClient;
  private final ExpressionParser expressionParser = new SpelExpressionParser();

  private static final String ASPECT_NAME = "DistributedLockAspect";

  @Around("@annotation(distributedLock)")
  public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
    // SpEL을 사용하여 락 키 생성
    String lockKey = generateLockKey(joinPoint, distributedLock.key());

    RLock lock = redissonClient.getLock(lockKey);
    boolean acquired = false;

    try {
      // 락 획득 시도 (재시도 로직 포함)
      acquired = tryLock(lock, distributedLock);

      if (!acquired) {
        String message = String.format("락 획득 실패 - 키: %s, 대기시간: %dms", lockKey, distributedLock.waitTime());
        LoggerFactory.common().logWarning(ASPECT_NAME, message);
        throw new LockAcquisitionException(message);
      }

      LoggerFactory.common()
          .logInfo(ASPECT_NAME, String.format("락 획득 성공 - 키: %s", lockKey));

      // 실제 메서드 실행
      return joinPoint.proceed();

    } finally {
      // 락 해제
      if (acquired && lock.isHeldByCurrentThread()) {
        try {
          lock.unlock();
          LoggerFactory.common().logInfo(ASPECT_NAME, String.format("락 해제 성공 - 키: %s", lockKey));
        } catch (Exception e) {
          LoggerFactory.common()
              .logError(ASPECT_NAME, String.format("락 해제 실패 - 키: %s", lockKey), e);
        }
      }
    }
  }

  /**
   * SpEL 표현식을 사용하여 락 키를 생성합니다.
   */
  private String generateLockKey(ProceedingJoinPoint joinPoint, String keyExpression) {
    try {
      EvaluationContext context = new StandardEvaluationContext();

      // 메서드 파라미터를 컨텍스트에 추가
      String[] paramNames = getParameterNames(joinPoint);
      Object[] args = joinPoint.getArgs();

      for (int i = 0; i < args.length; i++) {
        if (paramNames != null && i < paramNames.length) {
          context.setVariable(paramNames[i], args[i]);
        }
        context.setVariable("p" + i, args[i]);
      }

      Expression expression = expressionParser.parseExpression(keyExpression);
      Object result = expression.getValue(context);

      return result != null ? result.toString() : keyExpression;
    } catch (Exception e) {
      LoggerFactory.common()
          .logWarning(
              ASPECT_NAME,
              String.format("SpEL 표현식 파싱 실패, 원본 키 사용 - 표현식: %s, 오류: %s", keyExpression, e.getMessage()));
      return keyExpression;
    }
  }

  /**
   * 메서드 파라미터 이름을 가져옵니다.
   */
  private String[] getParameterNames(ProceedingJoinPoint joinPoint) {
    // Spring의 ParameterNameDiscoverer를 사용하거나, 간단하게 null 반환
    // 실제로는 Spring이 자동으로 파라미터 이름을 제공합니다 (-parameters 컴파일 옵션 필요)
    return null; // null이어도 SpEL의 #p0, #p1 형식으로 접근 가능
  }

  /**
   * 락 획득을 시도합니다. 재시도 로직을 포함합니다.
   */
  private boolean tryLock(RLock lock, DistributedLock distributedLock) {
    int retryCount = distributedLock.retry();
    long waitTime = distributedLock.waitTime();
    long leaseTime = distributedLock.leaseTime();

    for (int i = 0; i <= retryCount; i++) {
      try {
        boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        if (acquired) {
          return true;
        }

        if (i < retryCount) {
          LoggerFactory.common()
              .logInfo(
                  ASPECT_NAME,
                  String.format("락 획득 실패, 재시도 중... (%d/%d)", i + 1, retryCount));
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LoggerFactory.common()
            .logError(ASPECT_NAME, "락 획득 중 인터럽트 발생", e);
        return false;
      } catch (Exception e) {
        LoggerFactory.common().logError(ASPECT_NAME, "락 획득 중 오류 발생", e);
        if (i == retryCount) {
          return false;
        }
      }
    }

    return false;
  }
}

