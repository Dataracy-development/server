package com.dataracy.modules.common.support.lock;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;

import lombok.RequiredArgsConstructor;

/**
 * @DistributedLock 어노테이션을 처리하는 AOP Aspect
 *
 * <p>Redisson을 사용하여 분산 락을 획득하고 해제합니다. SpEL 표현식을 사용하여 락 키를 동적으로 생성합니다.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Order(1) // 트랜잭션보다 먼저 실행되도록 설정
public class DistributedLockAspect {
  private final RedissonClient redissonClient;
  private final ExpressionParser expressionParser = new SpelExpressionParser();

  private static final String ASPECT_NAME = "DistributedLockAspect";

  /**
   * 분산 락이 적용된 메서드 실행을 가로채어 락을 획득한 후 원래 메서드를 실행합니다.
   *
   * <p>분산 락 키는 {@link DistributedLock} 어노테이션의 SpEL 표현식을 기반으로 생성됩니다. 락 획득에 실패하거나 내부 실행 중 예외가 발생하면
   * 런타임 예외로 래핑되어 던져집니다.
   *
   * @return 원래 메서드의 실행 결과
   */
  @Around("@annotation(distributedLock)")
  public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock)
      throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = method.getName();

    // SpEL을 사용하여 락 키 생성
    String lockKey = generateLockKey(joinPoint, distributedLock.key());

    LoggerFactory.common()
        .logInfo(
            ASPECT_NAME,
            String.format(
                "[분산락] 락 진입 - class: %s method: %s key: %s waitTime: %dms leaseTime: %dms retry: %d",
                className,
                methodName,
                lockKey,
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.retry()));

    RLock lock = redissonClient.getLock(lockKey);
    boolean acquired = false;

    try {
      // 락 획득 시도 (재시도 로직 포함)
      acquired = tryLock(lock, distributedLock);

      if (!acquired) {
        String message =
            String.format("다른 사용자가 해당 자원에 접근 중입니다. (key: %s) 잠시 후 다시 시도해주세요.", lockKey);
        LoggerFactory.common()
            .logWarning(
                ASPECT_NAME,
                String.format(
                    "[분산락] 락 획득 실패 - class: %s method: %s key: %s",
                    className, methodName, lockKey));
        throw new LockAcquisitionException(message);
      }

      LoggerFactory.common()
          .logInfo(
              ASPECT_NAME,
              String.format(
                  "[분산락] 락 획득 성공, 원본 메서드 실행 시작 - class: %s method: %s key: %s",
                  className, methodName, lockKey));

      // 실제 메서드 실행
      try {
        Object result = joinPoint.proceed();
        LoggerFactory.common()
            .logInfo(
                ASPECT_NAME,
                String.format(
                    "[분산락] 원본 메서드 실행 완료 - class: %s method: %s key: %s",
                    className, methodName, lockKey));
        return result;
      } catch (BusinessException | CommonException e) {
        // 비즈니스/공통 예외는 그대로 전파
        LoggerFactory.common()
            .logError(
                ASPECT_NAME,
                String.format(
                    "[분산락] 원본 메서드에서 예외 발생 (전파) - class: %s method: %s key: %s errorCode: %s",
                    className, methodName, lockKey, e.getCode()),
                e);
        throw e;
      } catch (Throwable e) {
        // 기타 예외는 로깅 후 전파
        LoggerFactory.common()
            .logError(
                ASPECT_NAME,
                String.format(
                    "[분산락] 원본 메서드 실행 중 예상치 못한 예외 발생 - class: %s method: %s key: %s",
                    className, methodName, lockKey),
                e);
        throw e;
      }

    } finally {
      // 락 해제
      if (acquired && lock.isHeldByCurrentThread()) {
        try {
          lock.unlock();
          LoggerFactory.common()
              .logInfo(
                  ASPECT_NAME,
                  String.format(
                      "[분산락] 락 해제 성공 - class: %s method: %s key: %s",
                      className, methodName, lockKey));
        } catch (Exception e) {
          LoggerFactory.common()
              .logError(
                  ASPECT_NAME,
                  String.format(
                      "[분산락] 락 해제 실패 - class: %s method: %s key: %s",
                      className, methodName, lockKey),
                  e);
        }
      }
    }
  }

  /** SpEL 표현식을 사용하여 락 키를 생성합니다. */
  private String generateLockKey(ProceedingJoinPoint joinPoint, String keyExpression) {
    try {
      EvaluationContext context = new StandardEvaluationContext();

      // 메서드 파라미터를 컨텍스트에 추가
      Object[] args = joinPoint.getArgs();
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();

      // 파라미터 이름 추출 시도
      try {
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length && i < args.length; i++) {
          String paramName = parameters[i].getName();
          context.setVariable(paramName, args[i]);
        }
      } catch (Exception e) {
        // 파라미터 이름 추출 실패 시 인덱스로 접근
        LoggerFactory.common().logWarning(ASPECT_NAME, "파라미터 이름 추출 실패, 인덱스로 접근: " + e.getMessage());
      }

      // p0, p1 형식으로도 접근 가능하도록 설정
      for (int i = 0; i < args.length; i++) {
        context.setVariable("p" + i, args[i]);
      }

      Expression expression = expressionParser.parseExpression(keyExpression);
      Object result = expression.getValue(context);

      if (result == null || result.toString().trim().isEmpty()) {
        LoggerFactory.common()
            .logError(
                ASPECT_NAME,
                String.format("SpEL 표현식 파싱 결과가 유효하지 않음 - 표현식: %s, 결과: %s", keyExpression, result));
        throw new LockAcquisitionException(
            "SpEL로 생성된 락 키가 null 또는 빈 문자열입니다. expression: " + keyExpression);
      }

      String lockKey = result.toString();
      LoggerFactory.common()
          .logInfo(
              ASPECT_NAME, String.format("락 키 생성 완료 - 표현식: %s, 키: %s", keyExpression, lockKey));
      return lockKey;

    } catch (org.springframework.expression.spel.SpelEvaluationException e) {
      LoggerFactory.common()
          .logError(ASPECT_NAME, String.format("SpEL 표현식 파싱 실패 - 표현식: %s", keyExpression), e);
      throw new LockAcquisitionException(
          "분산 락 키 SpEL 파싱 실패: "
              + keyExpression
              + " (method: "
              + joinPoint.getSignature().getName()
              + ")",
          e);
    } catch (Exception e) {
      LoggerFactory.common()
          .logError(ASPECT_NAME, String.format("락 키 생성 중 오류 발생 - 표현식: %s", keyExpression), e);
      throw new LockAcquisitionException("락 키 생성 실패: " + keyExpression, e);
    }
  }

  /** 락 획득을 시도합니다. 재시도 로직을 포함합니다. */
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
              .logInfo(ASPECT_NAME, String.format("락 획득 실패, 재시도 중... (%d/%d)", i + 1, retryCount));
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LoggerFactory.common().logError(ASPECT_NAME, "락 획득 중 인터럽트 발생", e);
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
