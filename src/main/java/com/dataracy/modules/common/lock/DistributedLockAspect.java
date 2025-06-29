package com.dataracy.modules.common.lock;

import io.lettuce.core.dynamic.support.ParameterNameDiscoverer;
import io.lettuce.core.dynamic.support.StandardReflectionParameterNameDiscoverer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

// common/lock/aop/DistributedLockAspect.java
@Aspect
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

        // 파라미터 이름과 값 추출
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // SpEL → 실제 key 문자열 계산
        String key = parser.parseExpression(lock.key()).getValue(context, String.class);

        return lockManager.execute(
                key,
                lock.waitTime(),
                lock.leaseTime(),
                lock.retry(),
                () -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
