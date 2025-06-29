package com.dataracy.modules.common.lock;

import io.lettuce.core.dynamic.support.ParameterNameDiscoverer;
import io.lettuce.core.dynamic.support.StandardReflectionParameterNameDiscoverer;
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

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonDistributedLockManager lockManager;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new StandardReflectionParameterNameDiscoverer();

    @Around("@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock lock) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 1. SpEL 키 계산
        String key = generateLockKey(method, joinPoint.getArgs(), lock);

        log.debug("[LOCK] AOP 시작 - method: {}, key: {}", method.getName(), key);

        try {
            return lockManager.execute(
                    key,
                    lock.waitTime(),
                    lock.leaseTime(),
                    lock.retry(),
                    () -> {
                        try {
                            return joinPoint.proceed();
                        } catch (Throwable e) {
                            log.error("[LOCK] 비즈니스 로직 실행 중 예외 - key: {}, message: {}", key, e.getMessage(), e);
                            throw new LockAcquisitionException("분산 락을 수행 중 예외가 발생했습니다.", e);
                        }
                    }
            );
        } catch (Exception e) {
            log.error("[LOCK] 락 획득/실행 실패 - key: {}, message: {}", key, e.getMessage(), e);
            throw new LockAcquisitionException("분산 락 획득 또는 해제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * SpEL 기반으로 분산 락 키를 동적으로 계산
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
            log.error("[LOCK] SpEL 키 파싱 오류 - expression: {}", lock.key(), e);
            throw new LockAcquisitionException("분산 락 키 SpEL 파싱 실패: " + lock.key(), e);
        }
    }
}
