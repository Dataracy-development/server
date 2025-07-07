package com.dataracy.modules.common.support.lock;

import com.dataracy.modules.common.exception.BusinessException;
import io.lettuce.core.dynamic.support.ParameterNameDiscoverer;
import io.lettuce.core.dynamic.support.StandardReflectionParameterNameDiscoverer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 분산락 AOP를 설계한다.
 * DistributedLock 어노테이션을 통하여 분산락을 시행
 * Spel문법을 통하여 레디스 키를 설정하며,
 * 시간과 시도 횟수를 설정할 수 있다.
 */
@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {
    private final RedissonDistributedLockManager lockManager;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new StandardReflectionParameterNameDiscoverer();

    @PostConstruct
    public void init() {
        log.info("[AOP] DistributedLockAspect 초기화 완료");
    }

    @Around("@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock lock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String key = generateLockKey(method, joinPoint.getArgs(), lock);

        log.info("[AOP] 분산 락 진입 - method: {}, key: {}", method.getName(), key);

        try {
            return lockManager.execute(
                    key,
                    lock.waitTime(),
                    lock.leaseTime(),
                    lock.retry(),
                    () -> {
                        log.info("[AOP] 분산 락 진입 - method: {}, key: {}", method.getName(), key);
                        try {
                            return proceedSafely(joinPoint);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (BusinessException e) {
            // 커스텀 비즈니스 예외는 그대로 전파
            log.warn("[AOP] 비즈니스 예외 - key: {}, message: {}", key, e.getMessage());
            throw e;
        } catch (Throwable e) {
            // 시스템 예외만 RuntimeException으로 감싸기
            log.error("[AOP] 락 내부 로직 예외 - key: {}", key, e);
            throw new RuntimeException(e);
        }
    }

    private Object proceedSafely(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    // 키 생성
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
            log.error("[AOP] SpEL 키 파싱 오류 - expression: {}", lock.key(), e);
            throw new LockAcquisitionException("분산 락 키 SpEL 파싱 실패: " + lock.key(), e);
        }
    }
}
